.PHONY: up down restart build logs ps clean test

## 전체 스택 실행 (app + MySQL)
up:
	docker compose up -d

## 전체 스택 중지 및 컨테이너 제거
down:
	docker compose down

## 이미지 재빌드 후 재시작
restart:
	docker compose down
	docker compose up -d --build

## 이미지 빌드만
build:
	docker compose build

## 컨테이너 로그 실시간 확인
logs:
	docker compose logs -f

## app 로그만
logs-app:
	docker compose logs -f app

## MySQL 로그만
logs-db:
	docker compose logs -f mysql

## 실행 중인 컨테이너 목록
ps:
	docker compose ps

## 컨테이너 + 볼륨 모두 제거 (DB 데이터 초기화)
clean:
	docker compose down -v

## 도메인 유닛 테스트
test:
	./gradlew :domain:test

## 전체 빌드
build-gradle:
	./gradlew :app:bootJar

## 로컬 실행 (MySQL 별도 기동 필요)
run:
	./gradlew :app:bootRun
