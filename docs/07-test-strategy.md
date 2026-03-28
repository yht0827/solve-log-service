# 테스트 전략

계층별 테스트 범위, 핵심 설계, 동시성 검증 방법을 정리한다.

---

## 목차

1. [계층별 테스트 구조](#1-계층별-테스트-구조)
2. [레이어별 핵심 설계](#2-레이어별-핵심-설계)
3. [동시성 테스트 — ConcurrentSubmitTest](#3-동시성-테스트--concurrentsubmittest)

---

## 1. 계층별 테스트 구조

| 모듈 | 어노테이션 | DB | 검증 범위 |
|------|-----------|-----|-----------|
| `domain` | 없음 (순수 JUnit) | 없음 | 채점 로직, 필터링, 정답률 계산 |
| `application` | `@ExtendWith(MockitoExtension.class)` | 없음 | 서비스 오케스트레이션, 예외 분기 |
| `infrastructure` | `@SpringBootTest(webEnvironment = NONE)` | Testcontainers MySQL | JPA 쿼리, 인덱스, 집계 결과 |
| `api` | `@WebMvcTest` | 없음 | HTTP 요청/응답, 유효성 검사, 에러 코드 |
| `app` | `@SpringBootTest(webEnvironment = RANDOM_PORT)` | Testcontainers MySQL | E2E 흐름, 동시성 |

---

## 2. 레이어별 핵심 설계

### 2-1. domain — 순수 단위 테스트

외부 의존 없이 순수 JUnit으로 도메인 로직을 검증한다.

- `Problem.judge()` — 채점 로직
- `filterAvailable()` — 문제 필터링
- `correctRate()` — 정답률 계산

### 2-2. application — 서비스 로직 테스트

Mockito로 포트를 대체하여 DB 없이 서비스 로직과 예외 분기만 검증한다. BDDMockito의 `given` / `willThrow`를 사용하며, 모든 테스트는 `// given / when / then` 구조를 따른다.

### 2-3. infrastructure — 리포지토리 테스트

`RepositoryTestSupport`를 상속하여 Testcontainers MySQL 위에서 실행한다. 테스트 데이터는 `TestFixture`를 통해 JdbcTemplate으로 직접 삽입하여, 테스트 셋업이 테스트 대상(JPA 리포지토리)을 거치지 않도록 분리했다.

### 2-4. api — 컨트롤러 테스트

`@WebMvcTest` + `@MockitoBean` 조합으로 HTTP 상태 코드, 요청 유효성 검사, `GlobalExceptionHandler`의 에러 매핑을 검증한다.

### 2-5. app — E2E 통합 테스트

`AppTestSupport` + `RANDOM_PORT` 환경에서 `TestRestTemplate`으로 실제 HTTP 호출을 수행한다. Flyway 마이그레이션이 포함된 상태에서 전체 흐름을 검증한다.

---

## 3. 동시성 테스트 — ConcurrentSubmitTest

`CountDownLatch`로 10개 스레드를 동시에 출발시켜, 같은 사용자가 같은 문제를 동시에 제출하는 상황을 재현한다.

**검증 전제:** 서비스 레이어의 `existsByUserIdAndProblemId()` 사전 검사만으로는 레이스 컨디션을 막을 수 없다. DB의 `UNIQUE (user_id, problem_id)` 제약이 최종 방어선으로 동작하는지 확인한다.

**기대 결과**

| 응답 | 횟수 | 의미 |
|------|------|------|
| `200 OK` | 1회 | 최초 제출만 성공 |
| `409 CONFLICT` | 9회 | 나머지는 중복으로 차단 |