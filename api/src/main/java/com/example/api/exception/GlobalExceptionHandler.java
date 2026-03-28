package com.example.api.exception;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.api.response.ErrorResponse;
import com.example.domain.exception.DomainException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<ErrorResponse> handleDomainException(DomainException e) {
		ErrorCode errorCode = ErrorCode.valueOf(e.getErrorCode().name());
		return toResponseEntity(errorCode, e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
			.map(FieldError::getDefaultMessage)
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(ErrorCode.INVALID_INPUT.getMessage());
		return toResponseEntity(ErrorCode.INVALID_INPUT, message);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("Unexpected error", e);
		return toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
	}

	private ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, String message) {
		return ResponseEntity.status(errorCode.getStatus())
			.body(new ErrorResponse(errorCode.name(), message));
	}
}
