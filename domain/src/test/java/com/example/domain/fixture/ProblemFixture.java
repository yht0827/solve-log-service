package com.example.domain.fixture;

import static org.instancio.Select.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import org.instancio.Instancio;

import com.example.domain.entity.Problem;
import com.example.domain.entity.ProblemAnswer;
import com.example.domain.enums.AnswerType;

public class ProblemFixture {

	/** id만 지정된 기본 문제 (답 없음, 필터링 테스트용) */
	public static Problem withId(Long id) {
		return Instancio.of(Problem.class)
			.set(field(Problem.class, "id"), id)
			.set(field(Problem.class, "answerType"), AnswerType.MULTIPLE_CHOICE)
			.set(field(Problem.class, "choices"), new LinkedHashSet<>())
			.set(field(Problem.class, "answers"), new HashSet<>())
			.create();
	}

	/** 객관식 문제 */
	public static Problem multipleChoice(String... correctAnswers) {
		return create(AnswerType.MULTIPLE_CHOICE, correctAnswers);
	}

	/** 주관식 문제 */
	public static Problem subjective(String... correctAnswers) {
		return create(AnswerType.SUBJECTIVE, correctAnswers);
	}

	private static Problem create(AnswerType answerType, String... correctAnswers) {
		HashSet<ProblemAnswer> answers = Arrays.stream(correctAnswers)
			.map(v -> Instancio.of(ProblemAnswer.class)
				.ignore(field(ProblemAnswer.class, "problem"))
				.set(field(ProblemAnswer.class, "answerValue"), v)
				.create())
			.collect(Collectors.toCollection(HashSet::new));

		return Instancio.of(Problem.class)
			.set(field(Problem.class, "answerType"), answerType)
			.set(field(Problem.class, "choices"), new LinkedHashSet<>())
			.set(field(Problem.class, "answers"), answers)
			.create();
	}
}
