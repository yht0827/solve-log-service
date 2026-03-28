package com.example.application.dto;

import java.util.List;

import com.example.domain.entity.Choice;
import com.example.domain.entity.Problem;

public record ProblemQueryResult(
	Long problemId,
	String content,
	String answerType,
	List<ChoiceInfo> choices,
	Integer answerCorrectRate
) {

	public record ChoiceInfo(Integer sequence, String content) {

		public static ChoiceInfo from(Choice choice) {
			return new ChoiceInfo(choice.getSequence(), choice.getContent());
		}
	}

	public static ProblemQueryResult of(Problem problem, Integer answerCorrectRate) {
		return new ProblemQueryResult(
			problem.getId(),
			problem.getContent(),
			problem.getAnswerType().name(),
			problem.getChoices().stream().map(ChoiceInfo::from).toList(),
			answerCorrectRate
		);
	}
}
