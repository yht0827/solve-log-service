# 클래스 다이어그램

Solve Log Service의 계층 구조와 핵심 클래스 관계를 정리한다.

---

## 목차

1. [레이어드 구조 (Hexagonal)](#1-레이어드-구조-hexagonal)
2. [도메인 모델](#2-도메인-모델)
3. [예외 처리](#3-예외-처리)
4. [핵심 클래스 책임 요약](#4-핵심-클래스-책임-요약)
5. [JPA 페치 전략](#5-jpa-페치-전략)

---

## 1. 레이어드 구조 (Hexagonal)

```mermaid
classDiagram
    class ProblemController {
      +getRandomProblem(userId, request) ResponseEntity
      +skipProblem(userId, request) void
      +submitAnswer(userId, request) ResponseEntity
    }

    class SolveLogController {
      +getSolveDetail(userId, request) ResponseEntity
    }

    class GetRandomProblemUseCase {
      <<interface>>
      +getRandomProblem(chapterId, userId) ProblemQueryResult
    }

    class ProblemCommandUseCase {
      <<interface>>
      +submitAnswer(problemId, userId, userAnswers) SubmitResult
      +skipProblem(userId, chapterId, problemId) void
    }

    class GetSolveDetailUseCase {
      <<interface>>
      +getSolveDetail(userId, problemId) SolveDetailResult
    }

    class ProblemQueryService
    class ProblemCommandService
    class SolveLogQueryService

    class ChapterRepository {
      <<interface>>
      +findById(id) Optional~Chapter~
    }

    class ProblemRepository {
      <<interface>>
      +findById(id) Optional~Problem~
      +findByChapterId(chapterId) List~Problem~
    }

    class ProblemSolveLogRepository {
      <<interface>>
      +save(log) ProblemSolveLog
      +existsByUserIdAndProblemId(userId, problemId) boolean
      +findByUserIdAndProblemId(userId, problemId) Optional~ProblemSolveLog~
      +findSolvedProblemIdsByUserIdAndChapterId(userId, chapterId) List~Long~
      +countDistinctUsersByProblemId(problemId) long
      +countByProblemIdAndAnswerStatus(problemId, status) long
    }

    class UserProblemSkipRepository {
      <<interface>>
      +save(skip) UserProblemSkip
      +findByUserIdAndChapterId(userId, chapterId) Optional~UserProblemSkip~
    }

    ProblemController --> GetRandomProblemUseCase
    ProblemController --> ProblemCommandUseCase
    SolveLogController --> GetSolveDetailUseCase

    GetRandomProblemUseCase <|.. ProblemQueryService
    ProblemCommandUseCase <|.. ProblemCommandService
    GetSolveDetailUseCase <|.. SolveLogQueryService

    ProblemQueryService --> ChapterRepository
    ProblemQueryService --> ProblemRepository
    ProblemQueryService --> ProblemSolveLogRepository
    ProblemQueryService --> UserProblemSkipRepository

    ProblemCommandService --> ProblemRepository
    ProblemCommandService --> ProblemSolveLogRepository
    ProblemCommandService --> UserProblemSkipRepository

    SolveLogQueryService --> ProblemRepository
    SolveLogQueryService --> ProblemSolveLogRepository
```

---

## 2. 도메인 모델

```mermaid
classDiagram
    class BaseTimeEntity {
      +createdAt LocalDateTime
      +updatedAt LocalDateTime
    }

    class Chapter {
      +id Long
      +name String
    }

    class Problem {
      +id Long
      +chapterId Long
      +content String
      +answerType AnswerType
      +explanation String
      +choices Set~Choice~
      +answers Set~ProblemAnswer~
      +judge(userAnswers) AnswerStatus
      +getCorrectAnswerValues() List~String~
      +filterAvailable(problems, solvedIds, skippedId)$ List~Problem~
      +correctRate(totalSolvers, correctCount)$ Integer
    }

    class Choice {
      +id Long
      +problem Problem
      +sequence int
      +content String
    }

    class ProblemAnswer {
      +id Long
      +problem Problem
      +answerValue String
    }

    class ProblemSolveLog {
      +id Long
      +userId Long
      +problemId Long
      +answerStatus AnswerStatus
      +userAnswers List~UserAnswer~
      +create(userId, problemId, status)$ ProblemSolveLog
      +addUserAnswer(answer) void
    }

    class UserAnswer {
      +id Long
      +solveLog ProblemSolveLog
      +answerValue String
      +create(solveLog, value)$ UserAnswer
    }

    class UserProblemSkip {
      +id Long
      +userId Long
      +chapterId Long
      +problemId Long
      +skippedAt LocalDateTime
      +create(userId, chapterId, problemId)$ UserProblemSkip
      +updateProblemId(problemId) void
    }

    class Users {
      +id Long
      +name String
    }

    class AnswerType {
      <<enumeration>>
      MULTIPLE_CHOICE
      SUBJECTIVE
    }

    class AnswerStatus {
      <<enumeration>>
      CORRECT
      PARTIAL
      WRONG
    }

    BaseTimeEntity <|-- Chapter
    BaseTimeEntity <|-- Problem
    BaseTimeEntity <|-- ProblemSolveLog
    BaseTimeEntity <|-- Users

    Problem "1" o-- "*" Choice : choices
    Problem "1" o-- "*" ProblemAnswer : answers
    Problem --> AnswerType
    Problem --> AnswerStatus

    ProblemSolveLog "1" o-- "*" UserAnswer : userAnswers
    ProblemSolveLog --> AnswerStatus
```

---

## 3. 예외 처리

```mermaid
classDiagram
    class DomainException {
      <<abstract>>
    }

    class AlreadySolvedException
    class ProblemNotFoundException
    class ChapterNotFoundException
    class SolveLogNotFoundException
    class NoAvailableProblemException

    class GlobalExceptionHandler {
      +handleAlreadySolved() 409
      +handleProblemNotFound() 404
      +handleChapterNotFound() 404
      +handleSolveLogNotFound() 404
      +handleNoAvailableProblem() 404
      +handleDataIntegrityViolation() 409
      +handleMissingHeader() 401
      +handleMissingParam() 400
      +handleConstraintViolation() 400
      +handleValidation() 400
      +handleException() 500
    }

    class ErrorCode {
      <<enumeration>>
      NO_AVAILABLE_PROBLEM: 404
      PROBLEM_NOT_FOUND: 404
      SOLVE_LOG_NOT_FOUND: 404
      CHAPTER_NOT_FOUND: 404
      ALREADY_SOLVED: 409
      UNAUTHORIZED: 401
      INVALID_INPUT: 400
      INTERNAL_SERVER_ERROR: 500
    }

    DomainException <|-- AlreadySolvedException
    DomainException <|-- ProblemNotFoundException
    DomainException <|-- ChapterNotFoundException
    DomainException <|-- SolveLogNotFoundException
    DomainException <|-- NoAvailableProblemException

    GlobalExceptionHandler --> ErrorCode
```

---

## 4. 핵심 클래스 책임 요약

| 클래스 | 레이어 | 주요 책임 |
|--------|--------|-----------|
| `ProblemController` | API | 문제 관련 요청 검증, DTO 변환, 응답 생성 |
| `SolveLogController` | API | 풀이 이력 조회 요청 검증, 응답 생성 |
| `LoginUserIdArgumentResolver` | API | `X-User-Id` 헤더에서 `userId` 추출 |
| `GlobalExceptionHandler` | API | 도메인/검증 예외를 HTTP 에러 응답으로 변환 |
| `ProblemQueryService` | Application | 랜덤 문제 선택, 풀이 제외 필터링, 정답률 계산 |
| `ProblemCommandService` | Application | 중복 제출 검사, 채점, 풀이 이력 저장, 건너뛰기 upsert |
| `SolveLogQueryService` | Application | 풀이 상세 조회, 정답률 계산 |
| `Problem` | Domain | 채점 로직(`judge`), 풀이 가능 문제 필터링(`filterAvailable`), 정답률 계산(`correctRate`) |
| `ProblemSolveLog` | Domain | 풀이 이력 생성, 사용자 답변 추가 |
| `UserProblemSkip` | Domain | 건너뛰기 생성, `problemId` 갱신 |
| `ChapterRepositoryImpl` | Infrastructure | `chapter` 테이블 JPA 조회 |
| `ProblemRepositoryImpl` | Infrastructure | `problem` 테이블 JPA 조회 |
| `ProblemSolveLogRepositoryImpl` | Infrastructure | `problem_solve_log` 저장, 존재 확인, 집계 쿼리 |
| `UserProblemSkipRepositoryImpl` | Infrastructure | `user_problem_skip` upsert 및 조회 |

---

## 5. JPA 페치 전략

`choices`, `answers`, `userAnswers` 컬렉션은 모두 `EAGER`로 설정되어 있으나, JPQL `LEFT JOIN FETCH` 없이 조회하면 Hibernate가 secondary SELECT를 추가 실행한다.

| 엔티티 | 메서드 | 페치 방식 | 쿼리 수 변화 |
|--------|--------|-----------|-------------|
| `Problem` | `findById`, `findByChapterId` | `LEFT JOIN FETCH` + `DISTINCT` | 3 → 1 (두 컬렉션 동시 JOIN → Cartesian product를 `DISTINCT`로 제거) |
| `ProblemSolveLog` | `findByUserIdAndProblemId` | `LEFT JOIN FETCH` | 2 → 1 (단일 컬렉션이므로 `DISTINCT` 불필요) |