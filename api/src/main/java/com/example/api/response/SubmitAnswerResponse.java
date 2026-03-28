package com.example.api.response;

import java.util.List;

import com.example.application.dto.SubmitResult;

public record SubmitAnswerResponse(
	Long problemId,
	String answerStatus,
	String explanation,
	List<String> correctAnswers
) {

	public static SubmitAnswerResponse from(SubmitResult result) {
		return new SubmitAnswerResponse(
			result.problemId(),
			result.answerStatus().name(),
			result.explanation(),
			result.correctAnswers()
		);
	}
}
