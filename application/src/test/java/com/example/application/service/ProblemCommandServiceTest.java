package com.example.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.application.dto.SubmitResult;
import com.example.application.fixture.ProblemFixture;
import com.example.application.port.out.ProblemRepository;
import com.example.application.port.out.ProblemSolveLogRepository;
import com.example.application.port.out.UserProblemSkipRepository;
import com.example.domain.entity.ProblemSolveLog;
import com.example.domain.entity.UserProblemSkip;
import com.example.domain.enums.AnswerStatus;
import com.example.domain.exception.AlreadySolvedException;
import com.example.domain.exception.ProblemNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProblemCommandServiceTest {

	@Mock
	ProblemRepository problemRepository;
	@Mock
	ProblemSolveLogRepository problemSolveLogRepository;
	@Mock
	UserProblemSkipRepository userProblemSkipRepository;

	@InjectMocks
	ProblemCommandService sut;

	@Nested
	@DisplayName("submitAnswer()")
	class SubmitAnswerTest {

		@Test
		@DisplayName("이미 풀이한 문제면 AlreadySolvedException")
		void throws_AlreadySolvedException_when_already_solved() {
			// given
			given(problemSolveLogRepository.existsByUserIdAndProblemId(1L, 1L)).willReturn(true);

			// when & then
			assertThatThrownBy(() -> sut.submitAnswer(1L, 1L, List.of("1")))
				.isInstanceOf(AlreadySolvedException.class);
		}

		@Test
		@DisplayName("문제가 없으면 ProblemNotFoundException")
		void throws_ProblemNotFoundException_when_problem_not_found() {
			// given
			given(problemSolveLogRepository.existsByUserIdAndProblemId(1L, 1L)).willReturn(false);
			given(problemRepository.findById(1L)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sut.submitAnswer(1L, 1L, List.of("1")))
				.isInstanceOf(ProblemNotFoundException.class);
		}

		@Test
		@DisplayName("정답이면 CORRECT 반환")
		void returns_CORRECT_when_answer_is_correct() {
			// given
			given(problemSolveLogRepository.existsByUserIdAndProblemId(1L, 1L)).willReturn(false);
			given(problemRepository.findById(1L)).willReturn(Optional.of(ProblemFixture.multipleChoice(1L, "1")));
			given(problemSolveLogRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

			// when
			SubmitResult result = sut.submitAnswer(1L, 1L, List.of("1"));

			// then
			assertThat(result.answerStatus()).isEqualTo(AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("오답이면 WRONG 반환")
		void returns_WRONG_when_answer_is_wrong() {
			// given
			given(problemSolveLogRepository.existsByUserIdAndProblemId(1L, 1L)).willReturn(false);
			given(problemRepository.findById(1L)).willReturn(Optional.of(ProblemFixture.multipleChoice(1L, "1")));
			given(problemSolveLogRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

			// when
			SubmitResult result = sut.submitAnswer(1L, 1L, List.of("3"));

			// then
			assertThat(result.answerStatus()).isEqualTo(AnswerStatus.WRONG);
		}

		@Test
		@DisplayName("풀이 이력을 저장한다")
		void saves_solve_log() {
			// given
			given(problemSolveLogRepository.existsByUserIdAndProblemId(2L, 1L)).willReturn(false);
			given(problemRepository.findById(1L)).willReturn(Optional.of(ProblemFixture.multipleChoice(1L, "1")));
			given(problemSolveLogRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

			// when
			sut.submitAnswer(1L, 2L, List.of("1"));

			// then
			ArgumentCaptor<ProblemSolveLog> captor = ArgumentCaptor.forClass(ProblemSolveLog.class);
			then(problemSolveLogRepository).should().save(captor.capture());
			ProblemSolveLog saved = captor.getValue();
			assertThat(saved.getUserId()).isEqualTo(2L);
			assertThat(saved.getProblemId()).isEqualTo(1L);
			assertThat(saved.getAnswerStatus()).isEqualTo(AnswerStatus.CORRECT);
		}
	}

	@Nested
	@DisplayName("skipProblem()")
	class SkipProblemTest {

		@Test
		@DisplayName("기존 스킵 기록이 없으면 신규 생성한다")
		void creates_new_skip_when_no_existing_record() {
			// given
			given(userProblemSkipRepository.findByUserIdAndChapterId(1L, 1L)).willReturn(Optional.empty());
			given(userProblemSkipRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

			// when
			sut.skipProblem(1L, 1L, 5L);

			// then
			ArgumentCaptor<UserProblemSkip> captor = ArgumentCaptor.forClass(UserProblemSkip.class);
			then(userProblemSkipRepository).should().save(captor.capture());
			assertThat(captor.getValue().getProblemId()).isEqualTo(5L);
		}

		@Test
		@DisplayName("기존 스킵 기록이 있으면 problemId를 갱신한다")
		void updates_problemId_when_existing_record_found() {
			// given
			UserProblemSkip existing = UserProblemSkip.create(1L, 1L, 3L);
			given(userProblemSkipRepository.findByUserIdAndChapterId(1L, 1L)).willReturn(Optional.of(existing));
			given(userProblemSkipRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

			// when
			sut.skipProblem(1L, 1L, 7L);

			// then
			assertThat(existing.getProblemId()).isEqualTo(7L);
			then(userProblemSkipRepository).should().save(existing);
		}
	}
}
