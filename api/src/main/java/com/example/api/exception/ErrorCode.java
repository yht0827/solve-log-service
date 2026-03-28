package com.example.api.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	NO_AVAILABLE_PROBLEM(HttpStatus.NOT_FOUND, "더 이상 풀 수 있는 문제가 없습니다."),
	PROBLEM_NOT_FOUND(HttpStatus.NOT_FOUND, "문제를 찾을 수 없습니다."),
	SOLVE_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "풀이 이력을 찾을 수 없습니다."),
	CHAPTER_NOT_FOUND(HttpStatus.NOT_FOUND, "단원을 찾을 수 없습니다."),
	ALREADY_SOLVED(HttpStatus.CONFLICT, "이미 풀이한 문제입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 필요합니다."),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String message;
}
