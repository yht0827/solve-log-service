package com.example.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_problem_skip")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProblemSkip {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "chapter_id", nullable = false)
	private Long chapterId;

	@Column(name = "problem_id", nullable = false)
	private Long problemId;

	@Column(name = "skipped_at", nullable = false)
	private LocalDateTime skippedAt;

	public static UserProblemSkip create(Long userId, Long chapterId, Long problemId) {
		UserProblemSkip skip = new UserProblemSkip();
		skip.userId = userId;
		skip.chapterId = chapterId;
		skip.problemId = problemId;
		skip.skippedAt = LocalDateTime.now();
		return skip;
	}

	public void updateProblemId(Long problemId) {
		this.problemId = problemId;
		this.skippedAt = LocalDateTime.now();
	}
}
