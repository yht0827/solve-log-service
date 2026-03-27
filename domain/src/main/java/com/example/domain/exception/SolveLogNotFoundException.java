package com.example.domain.exception;

public class SolveLogNotFoundException extends DomainException {
	public SolveLogNotFoundException() {
		super(DomainErrorCode.SOLVE_LOG_NOT_FOUND);
	}
}
