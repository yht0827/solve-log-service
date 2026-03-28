package com.example.api.response;

import java.util.List;

import com.example.application.dto.ProblemQueryResult;

public record RandomProblemResponse(
	Long problemId,
	String content,
	String answerType,
	List<ChoiceInfo> choices,
	Integer answerCorrectRate
) {

	public record ChoiceInfo(Integer sequence, String content) {
	}

	public static RandomProblemResponse from(ProblemQueryResult result) {
		return new RandomProblemResponse(
			result.problemId(),
			result.content(),
			result.answerType(),
			result.choices().stream()
				.map(c -> new ChoiceInfo(c.sequence(), c.content()))
				.toList(),
			result.answerCorrectRate()
		);
	}
}
