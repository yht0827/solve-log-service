package com.example.api.exception;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.api.response.ErrorResponse;
import com.example.domain.exception.ChapterNotFoundException;
import com.example.domain.exception.NoAvailableProblemException;
import com.example.domain.exception.ProblemNotFoundException;
import com.example.domain.exception.SolveLogNotFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ProblemNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleProblemNotFound(ProblemNotFoundException e) {
		return toResponseEntity(ErrorCode.PROBLEM_NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(ChapterNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleChapterNotFound(ChapterNotFoundException e) {
		return toResponseEntity(ErrorCode.CHAPTER_NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(SolveLogNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleSolveLogNotFound(SolveLogNotFoundException e) {
		return toResponseEntity(ErrorCode.SOLVE_LOG_NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(NoAvailableProblemException.class)
	public ResponseEntity<ErrorResponse> handleNoAvailableProblem(NoAvailableProblemException e) {
		return toResponseEntity(ErrorCode.NO_AVAILABLE_PROBLEM, e.getMessage());
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException e) {
		return toResponseEntity(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException e) {
		return toResponseEntity(ErrorCode.INVALID_INPUT, e.getParameterName() + " 파라미터는 필수입니다.");
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
		String message = e.getConstraintViolations().stream()
			.map(ConstraintViolation::getMessage)
			.findFirst()
			.orElse(ErrorCode.INVALID_INPUT.getMessage());
		return toResponseEntity(ErrorCode.INVALID_INPUT, message);
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
