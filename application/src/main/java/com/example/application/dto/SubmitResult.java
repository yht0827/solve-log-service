package com.example.application.dto;

import java.util.List;

import com.example.domain.entity.Problem;
import com.example.domain.enums.AnswerStatus;

public record SubmitResult(
	Long problemId,
	AnswerStatus answerStatus,
	String explanation,
	List<String> correctAnswers
) {

	public static SubmitResult of(Problem problem, AnswerStatus answerStatus) {
		return new SubmitResult(
			problem.getId(),
			answerStatus,
			problem.getExplanation(),
			problem.getCorrectAnswerValues()
		);
	}
}
