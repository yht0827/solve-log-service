package com.example.domain.exception;

public class ChapterNotFoundException extends DomainException {
	public ChapterNotFoundException() {
		super(DomainErrorCode.CHAPTER_NOT_FOUND);
	}
}
