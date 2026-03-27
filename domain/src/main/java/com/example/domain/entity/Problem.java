package com.example.domain.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.domain.enums.AnswerStatus;
import com.example.domain.enums.AnswerType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "problem")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "chapter_id", nullable = false)
	private Long chapterId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(name = "answer_type", nullable = false)
	private AnswerType answerType;

	@Column(columnDefinition = "TEXT")
	private String explanation;

	@OneToMany(mappedBy = "problem", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OrderBy("sequence ASC")
	private List<Choice> choices = new ArrayList<>();

	@OneToMany(mappedBy = "problem", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<ProblemAnswer> answers = new ArrayList<>();

	/** 정답 값 목록을 반환한다. */
	public List<String> getCorrectAnswerValues() {
		return answers.stream()
			.map(ProblemAnswer::getAnswerValue)
			.collect(Collectors.toList());
	}

	/** 풀이 완료(solvedIds)와 마지막 건너뛰기(skippedId) 문제를 제외한 풀이 가능 문제를 반환한다. */
	public static List<Problem> filterAvailable(List<Problem> problems, Set<Long> solvedIds, Long skippedId) {
		return problems.stream()
			.filter(p -> !solvedIds.contains(p.getId()))
			.filter(p -> !p.getId().equals(skippedId))
			.toList();
	}

	private static final int MIN_SOLVERS_FOR_RATE = 30;

	/**
	 * 정답률을 계산한다.
	 */
	public static Integer correctRate(long totalSolvers, long correctCount) {
		if (totalSolvers < MIN_SOLVERS_FOR_RATE) {
			return null;
		}

		return (int)Math.round((double)correctCount / totalSolvers * 100);
	}

	/**
	 * 사용자 답변을 채점해 AnswerStatus를 반환한다.
	 * - 주관식: 대소문자·앞뒤 공백 무관하게 정답 중 하나와 일치하면 CORRECT
	 * - 객관식: 정답 집합과 완전 일치 CORRECT, 교집합 존재 PARTIAL, 없으면 WRONG
	 */
	public AnswerStatus judge(List<String> userAnswers) {
		List<String> correctAnswers = getCorrectAnswerValues();
		return answerType == AnswerType.SUBJECTIVE
			? judgeSubjective(userAnswers, correctAnswers)
			: judgeMultipleChoice(userAnswers, correctAnswers);
	}

	private AnswerStatus judgeSubjective(List<String> userAnswers, List<String> correctAnswers) {
		if (userAnswers.isEmpty()) {
			return AnswerStatus.WRONG;
		}

		boolean isCorrect = correctAnswers.stream()
			.anyMatch(ca -> ca.trim().equalsIgnoreCase(userAnswers.getFirst().trim()));

		if (isCorrect) {
			return AnswerStatus.CORRECT;
		}

		return AnswerStatus.WRONG;
	}

	private AnswerStatus judgeMultipleChoice(List<String> userAnswers, List<String> correctAnswers) {
		Set<String> correctSet = new HashSet<>(correctAnswers);
		Set<String> userSet = new HashSet<>(userAnswers);

		if (userSet.equals(correctSet)) {
			return AnswerStatus.CORRECT;
		}

		boolean hasAnyCorrect = userSet.stream().anyMatch(correctSet::contains);

		if (hasAnyCorrect) {
			return AnswerStatus.PARTIAL;
		}

		return AnswerStatus.WRONG;
	}
}
