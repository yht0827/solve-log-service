package com.example.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.domain.enums.AnswerStatus;
import com.example.domain.enums.AnswerType;

public class ProblemTest {

	private static Problem problemWithAnswers(AnswerType answerType, String... correctAnswerValues) {
		Problem problem = instantiate(Problem.class);
		ReflectionTestUtils.setField(problem, "answerType", answerType);

		List<ProblemAnswer> answers = java.util.Arrays.stream(correctAnswerValues)
			.map(v -> {
				ProblemAnswer pa = instantiate(ProblemAnswer.class);
				ReflectionTestUtils.setField(pa, "answerValue", v);
				return pa;
			})
			.collect(Collectors.toList());

		ReflectionTestUtils.setField(problem, "answers", answers);
		return problem;
	}

	private static Problem problemWithId(Long id) {
		Problem problem = instantiate(Problem.class);
		ReflectionTestUtils.setField(problem, "id", id);
		ReflectionTestUtils.setField(problem, "answers", new ArrayList<>());
		return problem;
	}

	private static <T> T instantiate(Class<T> clazz) {
		try {
			Constructor<T> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Nested
	@DisplayName("judge() - 주관식")
	class SubjectiveJudgeTest {

		@Test
		@DisplayName("정답과 일치하면 CORRECT")
		void correct_when_answer_matches() {
			Problem problem = problemWithAnswers(AnswerType.SUBJECTIVE, "파리");
			assertThat(problem.judge(List.of("파리"))).isEqualTo(AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("대소문자 무관하게 CORRECT")
		void correct_case_insensitive() {
			Problem problem = problemWithAnswers(AnswerType.SUBJECTIVE, "Paris");
			assertThat(problem.judge(List.of("paris"))).isEqualTo(AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("앞뒤 공백 무관하게 CORRECT")
		void correct_ignores_whitespace() {
			Problem problem = problemWithAnswers(AnswerType.SUBJECTIVE, "파리");
			assertThat(problem.judge(List.of("  파리  "))).isEqualTo(AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("오답이면 WRONG")
		void wrong_when_answer_does_not_match() {
			Problem problem = problemWithAnswers(AnswerType.SUBJECTIVE, "파리");
			assertThat(problem.judge(List.of("런던"))).isEqualTo(AnswerStatus.WRONG);
		}

		@Test
		@DisplayName("빈 답변이면 WRONG")
		void wrong_when_user_answer_is_empty() {
			Problem problem = problemWithAnswers(AnswerType.SUBJECTIVE, "파리");
			assertThat(problem.judge(List.of())).isEqualTo(AnswerStatus.WRONG);
		}
	}

	@Nested
	@DisplayName("judge() - 객관식")
	class MultipleChoiceJudgeTest {

		@Test
		@DisplayName("정답 집합과 정확히 일치하면 CORRECT")
		void correct_when_exact_match() {
			Problem problem = problemWithAnswers(AnswerType.MULTIPLE_CHOICE, "1", "3");
			assertThat(problem.judge(List.of("1", "3"))).isEqualTo(AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("정답 집합과 순서가 달라도 CORRECT")
		void correct_regardless_of_order() {
			Problem problem = problemWithAnswers(AnswerType.MULTIPLE_CHOICE, "1", "3");
			assertThat(problem.judge(List.of("3", "1"))).isEqualTo(AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("정답 중 일부만 맞으면 PARTIAL")
		void partial_when_some_answers_match() {
			Problem problem = problemWithAnswers(AnswerType.MULTIPLE_CHOICE, "1", "2", "3");
			assertThat(problem.judge(List.of("1", "2"))).isEqualTo(AnswerStatus.PARTIAL);
		}

		@Test
		@DisplayName("정답이 하나도 없으면 WRONG")
		void wrong_when_no_answer_matches() {
			Problem problem = problemWithAnswers(AnswerType.MULTIPLE_CHOICE, "1", "2");
			assertThat(problem.judge(List.of("3", "4"))).isEqualTo(AnswerStatus.WRONG);
		}

		@Test
		@DisplayName("단일 정답 문제에서 일치하면 CORRECT")
		void correct_single_answer() {
			Problem problem = problemWithAnswers(AnswerType.MULTIPLE_CHOICE, "2");
			assertThat(problem.judge(List.of("2"))).isEqualTo(AnswerStatus.CORRECT);
		}
	}

	@Nested
	@DisplayName("filterAvailable()")
	class FilterAvailableTest {

		@Test
		@DisplayName("풀이 완료한 문제를 제외한다")
		void excludes_solved_problems() {
			List<Problem> problems = List.of(problemWithId(1L), problemWithId(2L), problemWithId(3L));
			Set<Long> solvedIds = Set.of(1L, 2L);

			List<Problem> result = Problem.filterAvailable(problems, solvedIds, null);

			assertThat(result).hasSize(1);
			assertThat(result.get(0).getId()).isEqualTo(3L);
		}

		@Test
		@DisplayName("마지막으로 건너뛴 문제를 제외한다")
		void excludes_skipped_problem() {
			List<Problem> problems = List.of(problemWithId(1L), problemWithId(2L), problemWithId(3L));

			List<Problem> result = Problem.filterAvailable(problems, Set.of(), 2L);

			assertThat(result).hasSize(2);
			assertThat(result).noneMatch(p -> p.getId().equals(2L));
		}

		@Test
		@DisplayName("풀이 완료 + 건너뛰기를 동시에 제외한다")
		void excludes_both_solved_and_skipped() {
			List<Problem> problems = List.of(problemWithId(1L), problemWithId(2L), problemWithId(3L));

			List<Problem> result = Problem.filterAvailable(problems, Set.of(1L), 2L);

			assertThat(result).hasSize(1);
			assertThat(result.get(0).getId()).isEqualTo(3L);
		}

		@Test
		@DisplayName("필터 조건이 없으면 전체를 반환한다")
		void returns_all_when_no_filter() {
			List<Problem> problems = List.of(problemWithId(1L), problemWithId(2L));

			List<Problem> result = Problem.filterAvailable(problems, Set.of(), null);

			assertThat(result).hasSize(2);
		}

		@Test
		@DisplayName("모든 문제가 필터되면 빈 리스트를 반환한다")
		void returns_empty_when_all_filtered() {
			List<Problem> problems = List.of(problemWithId(1L), problemWithId(2L));

			List<Problem> result = Problem.filterAvailable(problems, Set.of(1L, 2L), null);

			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("correctRate()")
	class CorrectRateTest {

		@Test
		@DisplayName("풀이 인원이 기준(30명) 미만이면 null 반환")
		void returns_null_when_solvers_below_threshold() {
			assertThat(Problem.correctRate(29, 20)).isNull();
			assertThat(Problem.correctRate(0, 0)).isNull();
		}

		@Test
		@DisplayName("풀이 인원이 기준(30명)이면 계산한다")
		void calculates_when_solvers_at_threshold() {
			assertThat(Problem.correctRate(30, 15)).isEqualTo(50);
		}

		@Test
		@DisplayName("정답률을 올바르게 계산한다")
		void calculates_correct_rate() {
			assertThat(Problem.correctRate(100, 80)).isEqualTo(80);
			assertThat(Problem.correctRate(100, 0)).isEqualTo(0);
			assertThat(Problem.correctRate(100, 100)).isEqualTo(100);
		}

		@Test
		@DisplayName("소수점은 반올림한다")
		void rounds_decimal() {
			assertThat(Problem.correctRate(30, 10)).isEqualTo(33);  // 33.33...
			assertThat(Problem.correctRate(30, 20)).isEqualTo(67);  // 66.66...
		}
	}

	@Test
	@DisplayName("getCorrectAnswerValues()는 정답 값 목록을 반환한다")
	void getCorrectAnswerValues_returns_answer_values() {
		Problem problem = problemWithAnswers(AnswerType.MULTIPLE_CHOICE, "1", "3");
		assertThat(problem.getCorrectAnswerValues()).containsExactlyInAnyOrder("1", "3");
	}
}
