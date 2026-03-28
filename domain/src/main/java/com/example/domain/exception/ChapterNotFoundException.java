package com.example.domain.exception;

public class ChapterNotFoundException extends DomainException {
	public ChapterNotFoundException() {
		super("단원을 찾을 수 없습니다.");
	}
}
