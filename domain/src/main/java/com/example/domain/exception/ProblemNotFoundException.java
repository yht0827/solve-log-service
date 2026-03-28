package com.example.domain.exception;

public class ProblemNotFoundException extends DomainException {

	public ProblemNotFoundException() {
		super("문제를 찾을 수 없습니다.");
	}
}
