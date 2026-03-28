package com.example.domain.exception;

public class SolveLogNotFoundException extends DomainException {
	public SolveLogNotFoundException() {
		super("풀이 이력을 찾을 수 없습니다.");
	}
}
