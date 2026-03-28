package com.example.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.application.dto.SolveDetailResult;
import com.example.application.fixture.ProblemFixture;
import com.example.application.port.out.ProblemRepository;
import com.example.application.port.out.ProblemSolveLogRepository;
import com.example.domain.entity.ProblemSolveLog;
import com.example.domain.enums.AnswerStatus;
import com.example.domain.exception.ProblemNotFoundException;
import com.example.domain.exception.SolveLogNotFoundException;

@ExtendWith(MockitoExtension.class)
class SolveLogQueryServiceTest {

	@Mock
	ProblemRepository problemRepository;
	@Mock
	ProblemSolveLogRepository problemSolveLogRepository;

	@InjectMocks
	SolveLogQueryService sut;

	@Nested
	@DisplayName("getSolveDetail()")
	class GetSolveDetailTest {

		@Test
		@DisplayName("문제가 없으면 ProblemNotFoundException")
		void throws_ProblemNotFoundException_when_problem_not_found() {
			// given
			given(problemRepository.findById(1L)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sut.getSolveDetail(1L, 1L))
				.isInstanceOf(ProblemNotFoundException.class);
		}

		@Test
		@DisplayName("풀이 이력이 없으면 SolveLogNotFoundException")
		void throws_SolveLogNotFoundException_when_solve_log_not_found() {
			// given
			given(problemRepository.findById(1L)).willReturn(Optional.of(ProblemFixture.withId(1L)));
			given(problemSolveLogRepository.findByUserIdAndProblemId(1L, 1L)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sut.getSolveDetail(1L, 1L))
				.isInstanceOf(SolveLogNotFoundException.class);
		}

		@Test
		@DisplayName("풀이 인원 미만이면 정답률 null")
		void returns_null_correctRate_when_solvers_below_threshold() {
			// given
			given(problemRepository.findById(1L)).willReturn(Optional.of(ProblemFixture.withId(1L)));
			given(problemSolveLogRepository.findByUserIdAndProblemId(1L, 1L)).willReturn(
				Optional.of(ProblemSolveLog.create(1L, 1L, AnswerStatus.CORRECT)));
			given(problemSolveLogRepository.countDistinctUsersByProblemId(1L)).willReturn(10L);
			given(problemSolveLogRepository.countByProblemIdAndAnswerStatus(1L, AnswerStatus.CORRECT)).willReturn(5L);

			// when
			SolveDetailResult result = sut.getSolveDetail(1L, 1L);

			// then
			assertThat(result.answerStatus()).isEqualTo(AnswerStatus.CORRECT);
			assertThat(result.answerCorrectRate()).isNull();
		}

		@Test
		@DisplayName("풀이 인원 충분하면 정답률 계산")
		void calculates_correctRate_when_enough_solvers() {
			// given
			given(problemRepository.findById(1L)).willReturn(Optional.of(ProblemFixture.withId(1L)));
			given(problemSolveLogRepository.findByUserIdAndProblemId(1L, 1L)).willReturn(
				Optional.of(ProblemSolveLog.create(1L, 1L, AnswerStatus.WRONG)));
			given(problemSolveLogRepository.countDistinctUsersByProblemId(1L)).willReturn(50L);
			given(problemSolveLogRepository.countByProblemIdAndAnswerStatus(1L, AnswerStatus.CORRECT)).willReturn(25L);

			// when
			SolveDetailResult result = sut.getSolveDetail(1L, 1L);

			// then
			assertThat(result.answerStatus()).isEqualTo(AnswerStatus.WRONG);
			assertThat(result.answerCorrectRate()).isEqualTo(50);
		}
	}
}
