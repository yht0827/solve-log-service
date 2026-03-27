package com.example.domain.exception;

import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {
	private final DomainErrorCode errorCode;

	protected DomainException(DomainErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
