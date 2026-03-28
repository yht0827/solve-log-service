package com.example.domain.exception;

public class AlreadySolvedException extends DomainException {
	public AlreadySolvedException() {
		super("이미 풀이한 문제입니다.");
	}
}
