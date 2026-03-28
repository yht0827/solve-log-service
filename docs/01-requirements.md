# 요구사항 정의서

Solve Log Service의 도메인 용어, API 명세, 도메인 규칙, 비기능 요구사항을 정의한다.

---

## 목차

1. [유비쿼터스 언어](#1-유비쿼터스-언어)
2. [시스템 개요](#2-시스템-개요)
3. [사용자 식별 — X-User-Id / @LoginUserId](#3-사용자-식별--x-user-id--loginuserid)
4. [API 명세](#4-api-명세)
5. [도메인 규칙](#5-도메인-규칙)
6. [비기능적 요구사항](#6-비기능적-요구사항)

---

## 1. 유비쿼터스 언어

| 한글 용어 | 영문 용어 | 설명 |
|-----------|-----------|------|
| 단원 | Chapter | 문제를 묶는 학습 단위 |
| 문제 | Problem | 사용자가 풀이하는 단위 항목 |
| 선택지 | Choice | 객관식 문제의 보기 항목 (`sequence` 순 정렬) |
| 정답 | ProblemAnswer | 문제에 대한 정답 값 (복수 정답 가능) |
| 사용자 | User | 문제를 풀이하는 주체 (`X-User-Id` 헤더로 식별) |
| 풀이 이력 | ProblemSolveLog | 사용자의 문제 제출 결과 기록 |
| 사용자 답변 | UserAnswer | 풀이 이력에 속하는 제출 답변 값 |
| 건너뛰기 | Skip | 현재 문제를 넘기고 다른 문제로 이동하는 행위 |
| 건너뛰기 기록 | UserProblemSkip | 단원별 직전 건너뛰기 문제를 기록하는 엔티티 |
| 정답 여부 | AnswerStatus | `CORRECT` / `PARTIAL` / `WRONG` |
| 문제 유형 | AnswerType | `MULTIPLE_CHOICE` / `SUBJECTIVE` |
| 정답률 | CorrectRate | 전체 풀이 인원 대비 정답 비율 (30명 미만 시 `null`) |

---

## 2. 시스템 개요

### 2-1. 아키텍처

```text
Client
  → API (ProblemController / SolveLogController)
       X-User-Id 헤더로 사용자 식별
  → Application (ProblemQueryService / ProblemCommandService / SolveLogQueryService)
       포트(UseCase / Repository 인터페이스)를 통해 인프라에 의존하지 않음
  → Infrastructure (JPA Repository 구현체)
  → MySQL
```

### 2-2. 핵심 패턴

| 패턴 | 설명 |
|------|------|
| Hexagonal Architecture | `api → application(port) → infrastructure(adapter)` 계층 분리 |
| DB UNIQUE 기반 동시성 제어 | `problem_solve_log(user_id, problem_id)` UNIQUE → 중복 제출 시 `DataIntegrityViolationException` → 409 |
| 건너뛰기 단건 유지 (Upsert) | `user_problem_skip(user_id, chapter_id)` UNIQUE → 단원당 직전 1건만 유지 |
| 정답률 지연 노출 | 풀이 인원 30명 미만이면 `answerCorrectRate: null` |

### 2-3. 모듈 구성

| 모듈 | 책임 |
|------|------|
| `domain` | 엔티티, 도메인 규칙 (`Problem`, `ProblemSolveLog`, `UserProblemSkip` 등) |
| `application` | 유스케이스, 서비스, 포트 인터페이스 정의 |
| `infrastructure` | JPA 리포지토리 구현체 |
| `api` | HTTP Controller, 요청/응답 DTO, 예외 핸들러 |
| `app` | Spring Boot 실행 진입점 |

---

## 3. 사용자 식별 — X-User-Id / @LoginUserId

모든 API는 `X-User-Id` 헤더로 사용자를 식별한다. Spring Security 없이 커스텀 `HandlerMethodArgumentResolver`로 구현했다.

### 3-1. 처리 흐름

```text
요청 헤더: X-User-Id: 1
  → LoginUserIdArgumentResolver.resolveArgument()
    → Long.parseLong("1") = 1L
      → 컨트롤러 파라미터: @LoginUserId Long userId
```

### 3-2. 구성 요소

| 클래스 | 위치 | 역할 |
|--------|------|------|
| `@LoginUserId` | `api/auth` | 파라미터 어노테이션. `Long` 타입에만 적용 |
| `LoginUserIdArgumentResolver` | `api/auth` | `X-User-Id` 헤더 값을 `Long`으로 변환하여 파라미터에 바인딩 |
| `WebMvcConfig` | `api/config` | `addArgumentResolvers()`로 resolver 등록 |
| `GlobalExceptionHandler` | `api/exception` | `MissingRequestHeaderException` → 401 매핑 |

### 3-3. 예외 처리

헤더가 없거나 빈 값이면 resolver에서 `MissingRequestHeaderException`을 던진다. `GlobalExceptionHandler`가 이를 잡아 `401 UNAUTHORIZED`로 변환한다.

| 조건 | 예외 | HTTP 응답 |
|------|------|-----------|
| `X-User-Id` 헤더 없음 | `MissingRequestHeaderException` | `401 UNAUTHORIZED` |
| `X-User-Id` 빈 문자열 | `MissingRequestHeaderException` | `401 UNAUTHORIZED` |

### 3-4. 컨트롤러 사용 예시

```java
@GetMapping("/random")
public ResponseEntity<RandomProblemResponse> getRandomProblem(
    @LoginUserId Long userId,
    @Valid RandomProblemRequest request
) { ... }
```

---

## 4. API 명세

### API 전체 요약

| 도메인 | 기능 | METHOD | URI | 인증 |
|--------|------|--------|-----|------|
| 문제 | 랜덤 문제 조회 | GET | `/api/v1/problems/random` | O |
| 문제 | 문제 건너뛰기 | POST | `/api/v1/problems/skip` | O |
| 문제 | 문제 제출 | POST | `/api/v1/problems/submit` | O |
| 풀이 이력 | 풀이 상세 조회 | GET | `/api/v1/solve-logs` | O |

---

### 4-1. 랜덤 문제 조회

| METHOD | URI | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/v1/problems/random` | 단원 내 풀 수 있는 문제를 랜덤 조회 | 필요 (`X-User-Id`) |

#### 기능적 요구사항

- 요청한 단원(`chapterId`)이 존재하지 않으면 `404 CHAPTER_NOT_FOUND`를 반환한다.
- 단원 내 전체 문제에서 이미 풀었거나 직전에 건너뛴 문제를 제외한다.
- 제외 후 남은 문제가 없으면 `404 NO_AVAILABLE_PROBLEM`을 반환한다.
- 남은 문제 중 랜덤으로 1건을 반환한다.
- 풀이 인원 30명 이상인 경우에만 `answerCorrectRate`를 계산해 반환하며, 미만이면 `null`을 반환한다.

#### 입력 제약

| 위치 | 필드 | 제약 |
|------|------|------|
| 헤더 | `X-User-Id` | 필수 |
| 쿼리 | `chapterId` | 필수, 양수 |

#### Request

```http
GET /api/v1/problems/random?chapterId=1
X-User-Id: 1
```

#### Response Body (200 OK)

```json
{
  "problemId": 3,
  "content": "다음 중 Java의 특징이 아닌 것은?",
  "answerType": "MULTIPLE_CHOICE",
  "choices": [
    { "sequence": 1, "content": "플랫폼 독립성" },
    { "sequence": 2, "content": "포인터 직접 조작" },
    { "sequence": 3, "content": "객체지향" },
    { "sequence": 4, "content": "가비지 컬렉션" }
  ],
  "answerCorrectRate": 72
}
```

#### Fail Cases

| 케이스 | HTTP 상태 | 에러 코드 |
|--------|-----------|-----------|
| `X-User-Id` 헤더 누락 | `401` | `UNAUTHORIZED` |
| `chapterId` 누락 / 음수 | `400` | `INVALID_INPUT` |
| 단원 미존재 | `404` | `CHAPTER_NOT_FOUND` |
| 풀 수 있는 문제 없음 | `404` | `NO_AVAILABLE_PROBLEM` |

---

### 4-2. 문제 건너뛰기

| METHOD | URI | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/v1/problems/skip` | 현재 문제를 건너뛰고 기록을 저장 | 필요 (`X-User-Id`) |

#### 기능적 요구사항

- 동일 사용자·단원의 기존 건너뛰기 기록이 있으면 `problemId`를 갱신한다 (단원당 직전 1건 유지).
- 기록이 없으면 신규 생성한다.
- 성공 시 응답 바디 없이 `204 No Content`를 반환한다.

#### 입력 제약

| 위치 | 필드 | 제약 |
|------|------|------|
| 헤더 | `X-User-Id` | 필수 |
| Body | `problemId` | 필수, 양수 |
| Body | `chapterId` | 필수, 양수 |

#### Request

```http
POST /api/v1/problems/skip
X-User-Id: 1
Content-Type: application/json

{
  "problemId": 5,
  "chapterId": 1
}
```

#### Response (204 No Content)

응답 바디 없음.

#### Fail Cases

| 케이스 | HTTP 상태 | 에러 코드 |
|--------|-----------|-----------|
| `X-User-Id` 헤더 누락 | `401` | `UNAUTHORIZED` |
| `problemId` / `chapterId` 누락·음수 | `400` | `INVALID_INPUT` |

---

### 4-3. 문제 제출

| METHOD | URI | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/v1/problems/submit` | 문제 답안 제출 및 채점 | 필요 (`X-User-Id`) |

#### 기능적 요구사항

- 이미 풀이한 문제를 중복 제출하면 `409 ALREADY_SOLVED`를 반환한다.
  - 동시 요청이 들어올 경우 DB UNIQUE 제약(`user_id, problem_id`)으로 차단한다.
- 문제가 존재하지 않으면 `404 PROBLEM_NOT_FOUND`를 반환한다.
- 채점 후 풀이 이력(`ProblemSolveLog`)과 사용자 답변(`UserAnswer`)을 저장한다.
- 채점 결과, 정답 값, 해설을 응답으로 반환한다.

#### 채점 규칙

| 문제 유형 | 채점 방식 |
|-----------|-----------|
| `SUBJECTIVE` | 대소문자·앞뒤 공백 무관, 정답 중 하나와 일치하면 `CORRECT`, 아니면 `WRONG` |
| `MULTIPLE_CHOICE` | 정답 집합과 완전 일치 → `CORRECT`, 교집합 존재 → `PARTIAL`, 없으면 `WRONG` |

#### 입력 제약

| 위치 | 필드 | 제약 |
|------|------|------|
| 헤더 | `X-User-Id` | 필수 |
| Body | `problemId` | 필수, 양수 |
| Body | `userAnswers` | 필수, 1개 이상, 각 항목 공백 불가, 최대 100자 |

#### Request

```http
POST /api/v1/problems/submit
X-User-Id: 1
Content-Type: application/json

{
  "problemId": 3,
  "userAnswers": ["2"]
}
```

#### Response Body (200 OK)

```json
{
  "problemId": 3,
  "answerStatus": "CORRECT",
  "explanation": "Java는 포인터를 직접 조작하지 않습니다.",
  "correctAnswers": ["2"]
}
```

#### Fail Cases

| 케이스 | HTTP 상태 | 에러 코드 |
|--------|-----------|-----------|
| `X-User-Id` 헤더 누락 | `401` | `UNAUTHORIZED` |
| `problemId` 누락·음수 | `400` | `INVALID_INPUT` |
| `userAnswers` 비어있음 | `400` | `INVALID_INPUT` |
| 답변 공백 또는 100자 초과 | `400` | `INVALID_INPUT` |
| 이미 풀이한 문제 | `409` | `ALREADY_SOLVED` |
| 문제 미존재 | `404` | `PROBLEM_NOT_FOUND` |

---

### 4-4. 풀이 상세 조회

| METHOD | URI | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/v1/solve-logs` | 사용자가 풀었던 문제의 풀이 상세 조회 | 필요 (`X-User-Id`) |

#### 기능적 요구사항

- 문제가 존재하지 않으면 `404 PROBLEM_NOT_FOUND`를 반환한다.
- 해당 사용자의 풀이 이력이 없으면 `404 SOLVE_LOG_NOT_FOUND`를 반환한다.
- 풀이 인원 30명 이상인 경우에만 `answerCorrectRate`를 반환하며, 미만이면 `null`을 반환한다.

#### 입력 제약

| 위치 | 필드 | 제약 |
|------|------|------|
| 헤더 | `X-User-Id` | 필수 |
| 쿼리 | `problemId` | 필수, 양수 |

#### Request

```http
GET /api/v1/solve-logs?problemId=3
X-User-Id: 1
```

#### Response Body (200 OK)

```json
{
  "problemId": 3,
  "answerStatus": "CORRECT",
  "explanation": "Java는 포인터를 직접 조작하지 않습니다.",
  "correctAnswers": ["2"],
  "userAnswers": ["2"],
  "answerCorrectRate": 72
}
```

#### Fail Cases

| 케이스 | HTTP 상태 | 에러 코드 |
|--------|-----------|-----------|
| `X-User-Id` 헤더 누락 | `401` | `UNAUTHORIZED` |
| `problemId` 누락·음수 | `400` | `INVALID_INPUT` |
| 문제 미존재 | `404` | `PROBLEM_NOT_FOUND` |
| 풀이 이력 없음 | `404` | `SOLVE_LOG_NOT_FOUND` |

---

## 5. 도메인 규칙

### 5-1. 정답률 계산

- 기준: `COUNT(DISTINCT user_id) >= 30`인 경우에만 노출한다.
- 공식: `ROUND(correctCount / totalSolvers * 100)`
- 30명 미만이면 `answerCorrectRate: null`을 반환한다.

### 5-2. 건너뛰기 정책

- 단원당 직전 건너뛰기 1건만 유지한다.
- 새 문제를 건너뛰면 기존 행의 `problem_id`와 `skipped_at`을 갱신한다.
- 이전에 건너뛴 문제는 다시 풀이 대상이 된다.
- 랜덤 문제 조회 시 직전 건너뛴 문제(`problem_id`)는 제외한다.

### 5-3. 중복 제출 방지

- `problem_solve_log(user_id, problem_id)` UNIQUE 제약으로 1차 차단한다.
- 서비스 레이어에서 `existsByUserIdAndProblemId()`로 사전 검사한다.
- 동시 요청 시 DB 제약 위반 → `DataIntegrityViolationException` → `409 ALREADY_SOLVED`로 처리한다.

---

## 6. 비기능적 요구사항

### 6-1. 성능

- 랜덤 문제 조회: `problem`, `problem_solve_log`, `user_problem_skip` 인덱스로 단원 내 조회를 최적화한다.
- 정답률 집계: `problem_solve_log(problem_id, answer_status)` 커버링 인덱스 기반으로 COUNT 집계를 수행한다.

### 6-2. 신뢰성

- 동시 제출 중복 방지: DB UNIQUE 제약 + 서비스 레이어 사전 검사의 2중 방어 구조를 적용한다.

### 6-3. 확장성

- 포트 인터페이스(`ProblemRepository`, `ProblemSolveLogRepository` 등)로 인프라 구현체를 교체할 수 있다.
- `AnswerType` 추가 시 `Problem.judge()` 분기를 확장할 수 있다.
