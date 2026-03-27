package com.example.domain.exception;

public class ProblemNotFoundException extends DomainException {
	public ProblemNotFoundException() {
		super(DomainErrorCode.PROBLEM_NOT_FOUND);
	}
}
