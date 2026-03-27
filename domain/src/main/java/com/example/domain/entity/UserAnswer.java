package com.example.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "solve_log_id", nullable = false)
	private ProblemSolveLog solveLog;

	@Column(name = "answer_value", nullable = false)
	private String answerValue;

	public static UserAnswer create(ProblemSolveLog solveLog, String answerValue) {
		UserAnswer answer = new UserAnswer();
		answer.solveLog = solveLog;
		answer.answerValue = answerValue;
		return answer;
	}
}
