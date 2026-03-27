package com.example.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.domain.enums.AnswerStatus;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "problem_solve_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemSolveLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "problem_id", nullable = false)
	private Long problemId;

	@Enumerated(EnumType.STRING)
	@Column(name = "answer_status", nullable = false)
	private AnswerStatus answerStatus;

	@OneToMany(mappedBy = "solveLog", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserAnswer> userAnswers = new ArrayList<>();

	public static ProblemSolveLog create(Long userId, Long problemId, AnswerStatus answerStatus) {
		ProblemSolveLog log = new ProblemSolveLog();
		log.userId = userId;
		log.problemId = problemId;
		log.answerStatus = answerStatus;
		return log;
	}

	/** 사용자 답변을 풀이 이력에 추가한다. */
	public void addUserAnswer(UserAnswer userAnswer) {
		userAnswers.add(userAnswer);
	}

	/** 사용자가 제출한 답변 값 목록을 반환한다. */
	public List<String> getUserAnswerValues() {
		return userAnswers.stream()
			.map(UserAnswer::getAnswerValue)
			.toList();
	}
}
