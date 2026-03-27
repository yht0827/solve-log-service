package com.example.domain.exception;

public class NoAvailableProblemException extends DomainException {
	public NoAvailableProblemException() {
		super(DomainErrorCode.NO_AVAILABLE_PROBLEM);
	}

}
