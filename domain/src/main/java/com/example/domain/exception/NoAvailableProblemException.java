package com.example.domain.exception;

public class NoAvailableProblemException extends DomainException {
	public NoAvailableProblemException() {
		super("더 이상 풀 수 있는 문제가 없습니다.");
	}

}
