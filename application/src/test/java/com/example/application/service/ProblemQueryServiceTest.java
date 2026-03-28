package com.example.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.application.dto.ProblemQueryResult;
import com.example.application.fixture.ChapterFixture;
import com.example.application.fixture.ProblemFixture;
import com.example.application.port.out.ChapterRepository;
import com.example.application.port.out.ProblemRepository;
import com.example.application.port.out.ProblemSolveLogRepository;
import com.example.application.port.out.UserProblemSkipRepository;
import com.example.domain.entity.UserProblemSkip;
import com.example.domain.enums.AnswerStatus;
import com.example.domain.exception.ChapterNotFoundException;
import com.example.domain.exception.NoAvailableProblemException;

@ExtendWith(MockitoExtension.class)
class ProblemQueryServiceTest {

	@Mock
	ChapterRepository chapterRepository;
	@Mock
	ProblemRepository problemRepository;
	@Mock
	ProblemSolveLogRepository problemSolveLogRepository;
	@Mock
	UserProblemSkipRepository userProblemSkipRepository;

	@InjectMocks
	ProblemQueryService sut;

	@Nested
	@DisplayName("getRandomProblem()")
	class GetRandomProblemTest {

		@Test
		@DisplayName("단원이 존재하지 않으면 ChapterNotFoundException")
		void throws_ChapterNotFoundException_when_chapter_not_found() {
			// given
			given(chapterRepository.findById(1L)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sut.getRandomProblem(1L, 1L))
				.isInstanceOf(ChapterNotFoundException.class);
		}

		@Test
		@DisplayName("모든 문제를 풀었으면 NoAvailableProblemException")
		void throws_NoAvailableProblemException_when_all_solved() {
			// given
			given(chapterRepository.findById(1L)).willReturn(Optional.of(ChapterFixture.withId(1L)));
			given(problemRepository.findByChapterId(1L)).willReturn(
				List.of(ProblemFixture.withId(1L), ProblemFixture.withId(2L)));
			given(problemSolveLogRepository.findSolvedProblemIdsByUserIdAndChapterId(1L, 1L)).willReturn(
				List.of(1L, 2L));
			given(userProblemSkipRepository.findByUserIdAndChapterId(1L, 1L)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sut.getRandomProblem(1L, 1L))
				.isInstanceOf(NoAvailableProblemException.class);
		}

		@Test
		@DisplayName("풀었거나 건너뛴 문제를 제외하고 반환한다")
		void returns_problem_excluding_solved_and_skipped() {
			// given
			given(chapterRepository.findById(1L)).willReturn(Optional.of(ChapterFixture.withId(1L)));
			given(problemRepository.findByChapterId(1L)).willReturn(
				List.of(ProblemFixture.withId(1L), ProblemFixture.withId(2L), ProblemFixture.withId(3L)));
			given(problemSolveLogRepository.findSolvedProblemIdsByUserIdAndChapterId(1L, 1L)).willReturn(List.of(1L));
			given(userProblemSkipRepository.findByUserIdAndChapterId(1L, 1L)).willReturn(
				Optional.of(UserProblemSkip.create(1L, 1L, 2L)));
			given(problemSolveLogRepository.countDistinctUsersByProblemId(3L)).willReturn(0L);
			given(problemSolveLogRepository.countByProblemIdAndAnswerStatus(3L, AnswerStatus.CORRECT)).willReturn(0L);

			// when
			ProblemQueryResult result = sut.getRandomProblem(1L, 1L);

			// then
			assertThat(result.problemId()).isEqualTo(3L);
		}

		@Test
		@DisplayName("풀이 인원 30명 미만이면 정답률은 null")
		void returns_null_correctRate_when_solvers_below_threshold() {
			// given
			given(chapterRepository.findById(1L)).willReturn(Optional.of(ChapterFixture.withId(1L)));
			given(problemRepository.findByChapterId(1L)).willReturn(List.of(ProblemFixture.withId(1L)));
			given(problemSolveLogRepository.findSolvedProblemIdsByUserIdAndChapterId(1L, 1L)).willReturn(List.of());
			given(userProblemSkipRepository.findByUserIdAndChapterId(1L, 1L)).willReturn(Optional.empty());
			given(problemSolveLogRepository.countDistinctUsersByProblemId(1L)).willReturn(29L);
			given(problemSolveLogRepository.countByProblemIdAndAnswerStatus(1L, AnswerStatus.CORRECT)).willReturn(20L);

			// when
			ProblemQueryResult result = sut.getRandomProblem(1L, 1L);

			// then
			assertThat(result.answerCorrectRate()).isNull();
		}

		@Test
		@DisplayName("풀이 인원 30명 이상이면 정답률을 계산한다")
		void calculates_correctRate_when_enough_solvers() {
			// given
			given(chapterRepository.findById(1L)).willReturn(Optional.of(ChapterFixture.withId(1L)));
			given(problemRepository.findByChapterId(1L)).willReturn(List.of(ProblemFixture.withId(1L)));
			given(problemSolveLogRepository.findSolvedProblemIdsByUserIdAndChapterId(1L, 1L)).willReturn(List.of());
			given(userProblemSkipRepository.findByUserIdAndChapterId(1L, 1L)).willReturn(Optional.empty());
			given(problemSolveLogRepository.countDistinctUsersByProblemId(1L)).willReturn(100L);
			given(problemSolveLogRepository.countByProblemIdAndAnswerStatus(1L, AnswerStatus.CORRECT)).willReturn(80L);

			// when
			ProblemQueryResult result = sut.getRandomProblem(1L, 1L);

			// then
			assertThat(result.answerCorrectRate()).isEqualTo(80);
		}
	}
}
