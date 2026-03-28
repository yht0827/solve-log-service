# 시퀀스 다이어그램

Solve Log Service의 주요 런타임 흐름을 시퀀스 다이어그램으로 정리한다.

---

## 목차

1. [랜덤 문제 조회](#1-랜덤-문제-조회)
2. [문제 건너뛰기](#2-문제-건너뛰기)
3. [문제 제출 (정상)](#3-문제-제출-정상)
4. [문제 제출 (동시 중복 요청)](#4-문제-제출-동시-중복-요청)
5. [풀이 상세 조회](#5-풀이-상세-조회)

---

## 1. 랜덤 문제 조회

```mermaid
sequenceDiagram
    participant C as Client
    participant API as ProblemController
    participant SVC as ProblemQueryService
    participant CR as ChapterRepository
    participant PR as ProblemRepository
    participant SLR as ProblemSolveLogRepository
    participant SKR as UserProblemSkipRepository
    participant DB as MySQL

    C->>API: GET /api/v1/problems/random?chapterId=1 (X-User-Id: 1)
    API->>SVC: getRandomProblem(chapterId=1, userId=1)

    SVC->>CR: findById(1)
    CR->>DB: SELECT chapter WHERE id=1
    DB-->>SVC: chapter or empty

    alt 단원 미존재
        SVC-->>API: ChapterNotFoundException
        API-->>C: 404 CHAPTER_NOT_FOUND
    else 단원 존재
        SVC->>PR: findByChapterId(1)
        PR->>DB: SELECT problem WHERE chapter_id=1
        DB-->>SVC: allProblems

        SVC->>SLR: findSolvedProblemIdsByUserIdAndChapterId(1, 1)
        SLR->>DB: SELECT problem_id FROM problem_solve_log WHERE user_id=1 AND problem IN (...)
        DB-->>SVC: solvedProblemIds

        SVC->>SKR: findByUserIdAndChapterId(1, 1)
        SKR->>DB: SELECT user_problem_skip WHERE user_id=1 AND chapter_id=1
        DB-->>SVC: lastSkip or empty

        SVC->>SVC: Problem.filterAvailable(allProblems, solvedIds, lastSkippedId)

        alt 풀 수 있는 문제 없음
            SVC-->>API: NoAvailableProblemException
            API-->>C: 404 NO_AVAILABLE_PROBLEM
        else 문제 존재
            SVC->>SVC: random.nextInt(available.size()) → selected

            SVC->>SLR: countDistinctUsersByProblemId(selectedId)
            SVC->>SLR: countByProblemIdAndAnswerStatus(selectedId, CORRECT)
            SVC->>SVC: Problem.correctRate(totalSolvers, correctCount)

            SVC-->>API: ProblemQueryResult
            API-->>C: 200 OK { problemId, content, answerType, choices, answerCorrectRate }
        end
    end
```

**핵심 포인트**

- 풀이 완료 문제와 직전 건너뛰기 문제를 동시에 제외한 후 랜덤으로 선택한다.
- 정답률은 풀이 인원 30명 이상일 때만 반환하며, 미만이면 `null`이다.

---

## 2. 문제 건너뛰기

```mermaid
sequenceDiagram
    participant C as Client
    participant API as ProblemController
    participant SVC as ProblemCommandService
    participant SKR as UserProblemSkipRepository
    participant DB as MySQL

    C->>API: POST /api/v1/problems/skip (X-User-Id: 1)
    API->>SVC: skipProblem(userId=1, chapterId=1, problemId=5)

    SVC->>SKR: findByUserIdAndChapterId(1, 1)
    SKR->>DB: SELECT user_problem_skip WHERE user_id=1 AND chapter_id=1
    DB-->>SVC: existing or empty

    alt 기존 기록 없음
        SVC->>SKR: save(UserProblemSkip.create(1, 1, 5))
        SKR->>DB: INSERT user_problem_skip
    else 기존 기록 있음
        SVC->>SVC: existing.updateProblemId(5)
        SVC->>SKR: save(existing)
        SKR->>DB: UPDATE user_problem_skip SET problem_id=5, skipped_at=NOW()
    end

    SVC-->>API: void
    API-->>C: 204 No Content
```

**핵심 포인트**

- 단원당 직전 건너뛰기 1건만 유지한다. 이전에 건너뛴 문제는 다음 랜덤 조회 시 다시 풀이 대상이 된다.

---

## 3. 문제 제출 (정상)

```mermaid
sequenceDiagram
    participant C as Client
    participant API as ProblemController
    participant SVC as ProblemCommandService
    participant SLR as ProblemSolveLogRepository
    participant PR as ProblemRepository
    participant DB as MySQL

    C->>API: POST /api/v1/problems/submit (X-User-Id: 1)
    API->>SVC: submitAnswer(problemId=3, userId=1, userAnswers=["2"])

    SVC->>SLR: existsByUserIdAndProblemId(1, 3)
    SLR->>DB: SELECT COUNT(*) FROM problem_solve_log WHERE user_id=1 AND problem_id=3
    DB-->>SVC: false

    SVC->>PR: findById(3)
    PR->>DB: SELECT problem (with choices, answers) WHERE id=3
    DB-->>SVC: problem

    SVC->>SVC: problem.judge(["2"]) → CORRECT
    SVC->>SLR: save(ProblemSolveLog + UserAnswer)
    SLR->>DB: INSERT problem_solve_log, INSERT user_answer

    SVC-->>API: SubmitResult
    API-->>C: 200 OK { problemId, answerStatus, explanation, correctAnswers }
```

**핵심 포인트**

- 서비스 레이어에서 중복 제출 여부를 먼저 확인한 후, 문제 조회 → 채점 순으로 처리한다.
- `ProblemSolveLog`와 `UserAnswer`는 동일 트랜잭션에서 저장된다.

---

## 4. 문제 제출 (동시 중복 요청)

```mermaid
sequenceDiagram
    participant T1 as Thread-1
    participant T2 as Thread-2
    participant SVC as ProblemCommandService
    participant DB as MySQL

    par 동시 요청
        T1->>SVC: submitAnswer(problemId=3, userId=1)
        T2->>SVC: submitAnswer(problemId=3, userId=1)
    end

    T1->>DB: existsByUserIdAndProblemId(1, 3) → false
    T2->>DB: existsByUserIdAndProblemId(1, 3) → false

    T1->>DB: INSERT problem_solve_log (user_id=1, problem_id=3) → 성공
    T2->>DB: INSERT problem_solve_log (user_id=1, problem_id=3) → UNIQUE constraint 위반

    DB-->>T2: DataIntegrityViolationException
    T2-->>Client: 409 ALREADY_SOLVED
    T1-->>Client: 200 OK
```

**핵심 포인트**

- 서비스 레이어의 `existsByUserIdAndProblemId()` 체크만으로는 동시 요청을 완전히 차단할 수 없다.
- `problem_solve_log(user_id, problem_id)` UNIQUE 제약이 최종 방어선으로 동작한다.
- `DataIntegrityViolationException`은 `GlobalExceptionHandler`에서 `409 ALREADY_SOLVED`로 변환된다.

---

## 5. 풀이 상세 조회

```mermaid
sequenceDiagram
    participant C as Client
    participant API as SolveLogController
    participant SVC as SolveLogQueryService
    participant PR as ProblemRepository
    participant SLR as ProblemSolveLogRepository
    participant DB as MySQL

    C->>API: GET /api/v1/solve-logs?problemId=3 (X-User-Id: 1)
    API->>SVC: getSolveDetail(userId=1, problemId=3)

    SVC->>PR: findById(3)
    PR->>DB: SELECT problem WHERE id=3
    DB-->>SVC: problem or empty

    alt 문제 미존재
        SVC-->>API: ProblemNotFoundException
        API-->>C: 404 PROBLEM_NOT_FOUND
    else 문제 존재
        SVC->>SLR: findByUserIdAndProblemId(1, 3)
        SLR->>DB: SELECT problem_solve_log WHERE user_id=1 AND problem_id=3
        DB-->>SVC: solveLog or empty

        alt 풀이 이력 없음
            SVC-->>API: SolveLogNotFoundException
            API-->>C: 404 SOLVE_LOG_NOT_FOUND
        else 이력 존재
            SVC->>SLR: countDistinctUsersByProblemId(3)
            SVC->>SLR: countByProblemIdAndAnswerStatus(3, CORRECT)
            SVC->>SVC: Problem.correctRate(totalSolvers, correctCount)

            SVC-->>API: SolveDetailResult
            API-->>C: 200 OK { problemId, answerStatus, explanation, correctAnswers, userAnswers, answerCorrectRate }
        end
    end
```

**핵심 포인트**

- 문제 존재 확인 → 풀이 이력 조회 순서로 처리한다.
- `userAnswers`는 사용자가 제출한 답변 목록이고, `correctAnswers`는 실제 정답 목록이다.