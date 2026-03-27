package com.example.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomainErrorCode {
	NO_AVAILABLE_PROBLEM("더 이상 풀 수 있는 문제가 없습니다."),
	PROBLEM_NOT_FOUND("문제를 찾을 수 없습니다."),
	SOLVE_LOG_NOT_FOUND("풀이 이력을 찾을 수 없습니다."),
	CHAPTER_NOT_FOUND("단원을 찾을 수 없습니다.");

	private final String message;
}
