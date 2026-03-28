package com.example.api.response;

import java.util.List;

import com.example.application.dto.SolveDetailResult;

public record SolveDetailResponse(
	Long problemId,
	String answerStatus,
	String explanation,
	List<String> correctAnswers,
	List<String> userAnswers,
	Integer answerCorrectRate
) {

	public static SolveDetailResponse from(SolveDetailResult result) {
		return new SolveDetailResponse(
			result.problemId(),
			result.answerStatus().name(),
			result.explanation(),
			result.correctAnswers(),
			result.userAnswers(),
			result.answerCorrectRate()
		);
	}
}
