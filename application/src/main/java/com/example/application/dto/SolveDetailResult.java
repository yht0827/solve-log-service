package com.example.application.dto;

import java.util.List;

import com.example.domain.entity.Problem;
import com.example.domain.entity.ProblemSolveLog;
import com.example.domain.enums.AnswerStatus;

public record SolveDetailResult(
	Long problemId,
	AnswerStatus answerStatus,
	String explanation,
	List<String> correctAnswers,
	List<String> userAnswers,
	Integer answerCorrectRate
) {

	public static SolveDetailResult of(Problem problem, ProblemSolveLog solveLog, Integer answerCorrectRate) {
		return new SolveDetailResult(
			problem.getId(),
			solveLog.getAnswerStatus(),
			problem.getExplanation(),
			problem.getCorrectAnswerValues(),
			solveLog.getUserAnswerValues(),
			answerCorrectRate
		);
	}
}
