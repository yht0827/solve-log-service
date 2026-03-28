# Solve Log Service

문제 풀이 및 풀이 이력 조회 REST API.

## 기술 스택

- Java 21 / Spring Boot 3.4.3
- Spring Data JPA / MySQL 8.0
- Flyway
- Gradle 멀티모듈
- Docker / Docker Compose
- Springdoc OpenAPI (Swagger UI)

## 모듈 구조

```
solve-log-service
├── domain          # 엔티티, 도메인 예외, 비즈니스 로직
├── application     # 유스케이스 인터페이스, 서비스 구현체, DTO
├── infrastructure  # JPA 구현체, Flyway 마이그레이션
├── api             # REST 컨트롤러, 요청/응답 모델, 예외 핸들러
└── app             # 애플리케이션 진입점
```

헥사고날 아키텍처(포트 & 어댑터) 기반. 의존성은 항상 `domain`을 향한다.

```
┌───────────────────────────────────────┐
│  Adapters                             │
│  api · infrastructure                 │
│  ┌─────────────────────────────────┐  │
│  │  Application                    │  │
│  │  (Use Cases / Ports)            │  │
│  │  ┌───────────────────────────┐  │  │
│  │  │  Domain                   │  │  │
│  │  │  (Entities / Rules)       │  │  │
│  │  └───────────────────────────┘  │  │
│  └─────────────────────────────────┘  │
└───────────────────────────────────────┘
```

| 모듈 | 역할 |
|------|------|
| `api` | 인바운드 어댑터. 컨트롤러가 `application/port/in` 인터페이스 호출 |
| `application` | 유스케이스 구현. 포트 인터페이스로만 인프라에 접근 |
| `domain` | 순수 도메인 모델. 외부 의존 없음 |
| `infrastructure` | 아웃바운드 어댑터. `application/port/out` 인터페이스 구현 |

## Docker 실행

```bash
docker compose up -d
```

## API

서버 실행 후 Swagger UI: `http://localhost:8080/swagger-ui.html`

모든 API는 `X-User-Id` 헤더로 사용자를 식별한다.

| METHOD | URI | 설명 |
|--------|-----|------|
| GET | `/api/v1/problems/random` | 단원 내 랜덤 문제 조회 |
| POST | `/api/v1/problems/skip` | 문제 건너뛰기 |
| POST | `/api/v1/problems/submit` | 문제 제출 및 채점 |
| GET | `/api/v1/solve-logs` | 풀이 상세 조회 |

## 테스트

| 모듈 | 방식 | 범위 |
|------|------|------|
| `domain` | 순수 유닛 | 채점, 필터링, 정답률 계산 |
| `application` | Mockito mock | 서비스 오케스트레이션 |
| `infrastructure` | Testcontainers (MySQL) | JPA 쿼리 검증 |
| `api` | `@WebMvcTest` | HTTP 요청/응답, 유효성 검사 |
| `app` | `@SpringBootTest` + Testcontainers | E2E (동시성 포함) |

```bash
./gradlew test
```

`http/` 디렉토리에 IntelliJ HTTP Client 수동 테스트 파일 포함.

## DB 마이그레이션

`infrastructure/src/main/resources/db/migration/`

| 파일 | 내용 |
|------|------|
| `V1__create_schema.sql` | 전체 테이블 DDL + 인덱스 |
| `V2__init_data.sql` | 초기 데이터 (챕터 2개, 문제 6개, 사용자 35명) |

## 문서

| 문서 | 설명 |
|------|------|
| [docs/01-requirements.md](docs/01-requirements.md) | 요구사항 정의, API 스펙, 도메인 규칙 |
| [docs/02-sequence-diagrams.md](docs/02-sequence-diagrams.md) | 주요 흐름 시퀀스 다이어그램 |
| [docs/03-class-diagrams.md](docs/03-class-diagrams.md) | 계층 구조, 클래스 관계 |
| [docs/04-erd.md](docs/04-erd.md) | ERD 및 테이블 상세 |
| [docs/05-performance.md](docs/05-performance.md) | 인덱스 전략, 벤치마크, 설계 결정 근거 |
| [docs/06-decisions.md](docs/06-decisions.md) | 멀티모듈, 헥사고날, Testcontainers 등 주요 기술 선택 이유 |
| [docs/07-test-strategy.md](docs/07-test-strategy.md) | 계층별 테스트 방식, Testcontainers 구성, 동시성 테스트 |
