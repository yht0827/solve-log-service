# 설계 결정 근거 (ADR)

프로젝트에서 내린 주요 설계 결정과 그 배경을 기록한다.

---

## 목차

1. [멀티모듈 구성](#1-멀티모듈-구성)
2. [헥사고날 아키텍처](#2-헥사고날-아키텍처)
3. [Testcontainers 도입](#3-testcontainers-도입)
4. [Redis 미적용](#4-redis-미적용)
5. [재풀기 미지원](#5-재풀기-미지원)
6. [인덱스 기반 성능 개선](#6-인덱스-기반-성능-개선)
7. [Command / Query 서비스 분리](#7-command--query-서비스-분리)
8. [N+1 문제 해결](#8-n1-문제-해결)

---

## 1. 멀티모듈 구성

| 항목 | 내용 |
|------|------|
| **문제** | 단일 모듈 구조에서는 의존성 방향 위반을 코드 리뷰에서만 감지할 수 있어, 실수가 런타임까지 넘어갈 위험이 있다. |
| **결정** | `domain` · `application` · `infrastructure` · `api` · `app` 5개 모듈로 분리하고, `build.gradle`에 의존성을 명시했다. |
| **결과** | 잘못된 방향의 참조가 **컴파일 오류**로 즉시 차단된다. |

**의존성 방향**

```
api → application → domain
infrastructure → application / domain
```

---

## 2. 헥사고날 아키텍처

| 항목 | 내용 |
|------|------|
| **문제** | `application`이 JPA 구현체에 직접 의존하면 DB 없이 서비스 로직을 테스트하기 어렵고, 구현체를 교체할 때 `application` 코드까지 함께 수정해야 한다. |
| **결정** | `application`은 포트 인터페이스(`ProblemRepository` 등)에만 의존하고, JPA 구현체를 알지 못하도록 격리했다. |
| **결과** | 서비스 테스트에서 Mockito로 포트를 대체해 DB 없이 검증할 수 있다. JPA 구현체를 변경해도 `application` 코드는 수정할 필요가 없다. |

---

## 3. Testcontainers 도입

| 항목 | 내용 |
|------|------|
| **문제** | H2는 UNIQUE 제약 위반 시 예외 동작, 인덱스 실행 계획, Flyway MySQL 전용 문법을 정확히 재현하지 못한다. |
| **결정** | H2 대신 Testcontainers를 통해 실제 MySQL 8.0 컨테이너에서 테스트를 실행한다. |
| **결과** | H2에서는 통과하지만 MySQL에서 실패하는 버그를 사전에 차단할 수 있다. |

---

## 4. Redis 미적용

| 항목 | 내용 |
|------|------|
| **배경** | `correctRate` 캐싱과 분산 락을 통한 성능 개선 가능성을 검토했다. |
| **결정** | Redis를 도입하지 않고, 커버링 인덱스로 대응했다. |
| **근거** | Redis 왕복 레이턴시가 오히려 **+1.8 ms**의 오버헤드를 발생시켰다. 커버링 인덱스만으로 충분한 성능이 확보되었다. |

> 측정 수치 → [05-performance.md](05-performance.md)

---

## 5. 재풀기 미지원

| 항목 | 내용 |
|------|------|
| **문제** | 재풀기를 허용하면 "실제 풀이"의 기준(최초 / 최근 / 최고점)을 정의해야 하고, `COUNT(DISTINCT user_id)` 기반 정답률 산출 로직이 모호해진다. |
| **결정** | `problem_solve_log(user_id, problem_id)`에 UNIQUE 제약을 걸어 재제출을 차단했다. |
| **결과** | 제약 조건 하나로 중복 제출 방지와 정답률 기준 단순화를 동시에 달성한다. |

---

## 6. 인덱스 기반 성능 개선

| 항목 | 내용 |
|------|------|
| **문제** | `correctRate` 산출을 위한 COUNT 쿼리가 `problem_solve_log` 테이블을 풀 스캔했다. |
| **결정** | `(problem_id, answer_status)` 복합 인덱스를 추가했다. |
| **결과** | Index-only scan으로 전환되어 평균 응답 시간이 **26% 감소**했다. |

> 벤치마크 수치 → [05-performance.md](05-performance.md)

---

## 7. Command / Query 서비스 분리

| 항목 | 내용 |
|------|------|
| **문제** | 읽기와 쓰기를 하나의 서비스에 합치면 클래스가 비대해지고, 단위 테스트에서 관계없는 의존성까지 모두 mock해야 한다. |
| **결정** | 쓰기(`ProblemCommandService`)와 읽기(`ProblemQueryService`, `SolveLogQueryService`)를 별도 클래스로 분리했다. |
| **결과** | CommandService 테스트에서 Query 관련 mock이 불필요하고, 그 반대도 마찬가지다. 각 서비스의 테스트가 간결해진다. |

---

## 8. N+1 문제 해결

| 항목 | 내용 |
|------|------|
| **문제** | `FetchType.EAGER` 컬렉션을 가진 엔티티를 `findById`로 조회하면, Hibernate가 추가 SELECT를 실행한다. |
| **결정** | `@Query` + `LEFT JOIN FETCH`를 사용해 단일 JOIN 쿼리로 처리했다. |

**개선 결과**

| 메서드 | 변경 전 | 변경 후 |
|--------|---------|---------|
| `Problem.findById` | 3 쿼리 | 1 쿼리 (`JOIN FETCH` + `DISTINCT`) |
| `ProblemSolveLog.findByUserIdAndProblemId` | 2 쿼리 | 1 쿼리 (`JOIN FETCH`) |