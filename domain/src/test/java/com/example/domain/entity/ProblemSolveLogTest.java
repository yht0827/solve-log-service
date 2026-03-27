package com.example.domain.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.domain.enums.AnswerStatus;

public class ProblemSolveLogTest {

	@Test
	@DisplayName("addUserAnswer() 후 getUserAnswerValues()는 추가된 값을 반환한다")
	void getUserAnswerValues_returns_added_values() {
		ProblemSolveLog log = ProblemSolveLog.create(1L, 1L, AnswerStatus.CORRECT);
		log.addUserAnswer(UserAnswer.create(log, "1"));
		log.addUserAnswer(UserAnswer.create(log, "3"));

		assertThat(log.getUserAnswerValues()).containsExactly("1", "3");
	}

	@Test
	@DisplayName("UserAnswer가 없으면 getUserAnswerValues()는 빈 리스트를 반환한다")
	void getUserAnswerValues_returns_empty_when_no_answers() {
		ProblemSolveLog log = ProblemSolveLog.create(1L, 1L, AnswerStatus.WRONG);

		assertThat(log.getUserAnswerValues()).isEmpty();
	}

	@Test
	@DisplayName("create()는 전달된 필드로 객체를 생성한다")
	void create_sets_fields() {
		ProblemSolveLog log = ProblemSolveLog.create(10L, 20L, AnswerStatus.PARTIAL);

		assertThat(log.getUserId()).isEqualTo(10L);
		assertThat(log.getProblemId()).isEqualTo(20L);
		assertThat(log.getAnswerStatus()).isEqualTo(AnswerStatus.PARTIAL);
	}
}
