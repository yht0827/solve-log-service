package com.example.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.domain.enums.AnswerStatus;
import com.example.domain.fixture.ProblemFixture;

public class ProblemTest {

	@Nested
	@DisplayName("judge() - 주관식")
	class SubjectiveJudgeTest {

		@Test
		@DisplayName("정답과 일치하면 CORRECT")
		void correct_when_answer_matches() {
			assertThat(ProblemFixture.subjective("파리").judge(List.of("파리"))).isEqualTo(AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("대소문자 무관하게 CORRECT")
		void correct_case_insensitive() {
			assertThat(ProblemFixture.subjective("Paris").judge(List.of("paris"))).isEqualTo(AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("앞뒤 공백 무관하게 CORRECT")
		void correct_ignores_whitespace() {
			assertThat(ProblemFixture.subjective("파리").judge(List.of("  파리  "))).isEqualTo(AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("오답이면 WRONG")
		void wrong_when_answer_does_not_match() {
			assertThat(ProblemFixture.subjective("파리").judge(List.of("런던"))).isEqualTo(AnswerStatus.WRONG);
		}

		@Test
		@DisplayName("빈 답변이면 WRONG")
		void wrong_when_user_answer_is_empty() {
			assertThat(ProblemFixture.subjective("파리").judge(List.of())).isEqualTo(AnswerStatus.WRONG);
		}

		@Test
		@DisplayName("여러 정답 중 두 번째 정답을 입력해도 CORRECT")
		void correct_when_second_answer_matches() {
			assertThat(ProblemFixture.subjective("파리", "프랑스 수도").judge(List.of("프랑스 수도"))).isEqualTo(
				AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("주관식은 첫 번째 입력값만 검사한다")
		void only_first_user_answer_is_evaluated() {
			assertThat(ProblemFixture.subjective("파리").judge(List.of("런던", "파리"))).isEqualTo(AnswerStatus.WRONG);
		}
	}

	@Nested
	@DisplayName("judge() - 객관식")
	class MultipleChoiceJudgeTest {

		@Test
		@DisplayName("정답 집합과 정확히 일치하면 CORRECT")
		void correct_when_exact_match() {
			assertThat(ProblemFixture.multipleChoice("1", "3").judge(List.of("1", "3"))).isEqualTo(
				AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("정답 집합과 순서가 달라도 CORRECT")
		void correct_regardless_of_order() {
			assertThat(ProblemFixture.multipleChoice("1", "3").judge(List.of("3", "1"))).isEqualTo(
				AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("정답 중 일부만 맞으면 PARTIAL")
		void partial_when_some_answers_match() {
			assertThat(ProblemFixture.multipleChoice("1", "2", "3").judge(List.of("1", "2"))).isEqualTo(
				AnswerStatus.PARTIAL);
		}

		@Test
		@DisplayName("정답이 하나도 없으면 WRONG")
		void wrong_when_no_answer_matches() {
			assertThat(ProblemFixture.multipleChoice("1", "2").judge(List.of("3", "4"))).isEqualTo(AnswerStatus.WRONG);
		}

		@Test
		@DisplayName("단일 정답 문제에서 일치하면 CORRECT")
		void correct_single_answer() {
			assertThat(ProblemFixture.multipleChoice("2").judge(List.of("2"))).isEqualTo(AnswerStatus.CORRECT);
		}

		@Test
		@DisplayName("정답 일부와 오답이 섞이면 PARTIAL")
		void partial_when_correct_and_wrong_answers_mixed() {
			assertThat(ProblemFixture.multipleChoice("1", "2", "3").judge(List.of("1", "4"))).isEqualTo(
				AnswerStatus.PARTIAL);
		}

		@Test
		@DisplayName("빈 답변이면 WRONG")
		void wrong_when_answer_is_empty() {
			assertThat(ProblemFixture.multipleChoice("1", "2").judge(List.of())).isEqualTo(AnswerStatus.WRONG);
		}

		@Test
		@DisplayName("정답 전부 + 오답 추가 선택하면 PARTIAL")
		void partial_when_all_correct_but_extra_wrong_included() {
			assertThat(ProblemFixture.multipleChoice("1", "2").judge(List.of("1", "2", "3"))).isEqualTo(
				AnswerStatus.PARTIAL);
		}
	}

	@Nested
	@DisplayName("filterAvailable()")
	class FilterAvailableTest {

		@Test
		@DisplayName("풀이 완료한 문제를 제외한다")
		void excludes_solved_problems() {
			List<Problem> problems = List.of(
				ProblemFixture.withId(1L), ProblemFixture.withId(2L), ProblemFixture.withId(3L));

			List<Problem> result = Problem.filterAvailable(problems, Set.of(1L, 2L), null);

			assertThat(result).hasSize(1);
			assertThat(result.getFirst().getId()).isEqualTo(3L);
		}

		@Test
		@DisplayName("마지막으로 건너뛴 문제를 제외한다")
		void excludes_skipped_problem() {
			List<Problem> problems = List.of(
				ProblemFixture.withId(1L), ProblemFixture.withId(2L), ProblemFixture.withId(3L));

			List<Problem> result = Problem.filterAvailable(problems, Set.of(), 2L);

			assertThat(result).hasSize(2);
			assertThat(result).noneMatch(p -> p.getId().equals(2L));
		}

		@Test
		@DisplayName("풀이 완료 + 건너뛰기를 동시에 제외한다")
		void excludes_both_solved_and_skipped() {
			List<Problem> problems = List.of(
				ProblemFixture.withId(1L), ProblemFixture.withId(2L), ProblemFixture.withId(3L));

			List<Problem> result = Problem.filterAvailable(problems, Set.of(1L), 2L);

			assertThat(result).hasSize(1);
			assertThat(result.getFirst().getId()).isEqualTo(3L);
		}

		@Test
		@DisplayName("필터 조건이 없으면 전체를 반환한다")
		void returns_all_when_no_filter() {
			List<Problem> problems = List.of(ProblemFixture.withId(1L), ProblemFixture.withId(2L));

			List<Problem> result = Problem.filterAvailable(problems, Set.of(), null);

			assertThat(result).hasSize(2);
		}

		@Test
		@DisplayName("모든 문제가 필터되면 빈 리스트를 반환한다")
		void returns_empty_when_all_filtered() {
			List<Problem> problems = List.of(ProblemFixture.withId(1L), ProblemFixture.withId(2L));

			List<Problem> result = Problem.filterAvailable(problems, Set.of(1L, 2L), null);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("건너뛴 문제가 이미 풀이 완료된 경우 중복 없이 제외한다")
		void excludes_skipped_problem_that_is_also_solved() {
			List<Problem> problems = List.of(
				ProblemFixture.withId(1L), ProblemFixture.withId(2L), ProblemFixture.withId(3L));

			List<Problem> result = Problem.filterAvailable(problems, Set.of(1L, 2L), 2L);

			assertThat(result).hasSize(1);
			assertThat(result.getFirst().getId()).isEqualTo(3L);
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
			assertThat(Problem.correctRate(30, 10)).isEqualTo(33);
			assertThat(Problem.correctRate(30, 20)).isEqualTo(67);
		}

		@Test
		@DisplayName("기준 미만이면 전원 정답이어도 null 반환")
		void returns_null_even_if_all_correct_but_below_threshold() {
			assertThat(Problem.correctRate(29, 29)).isNull();
		}

		@Test
		@DisplayName("정답률이 낮은 경우 반올림해서 반환한다")
		void rounds_low_correct_rate() {
			assertThat(Problem.correctRate(30, 1)).isEqualTo(3);
		}

		@Test
		@DisplayName("0.5 이상 소수점은 올림한다")
		void rounds_up_at_half() {
			assertThat(Problem.correctRate(200, 101)).isEqualTo(51);
		}
	}

	@Test
	@DisplayName("getCorrectAnswerValues()는 정답 값 목록을 반환한다")
	void getCorrectAnswerValues_returns_answer_values() {
		assertThat(ProblemFixture.multipleChoice("1", "3").getCorrectAnswerValues())
			.containsExactlyInAnyOrder("1", "3");
	}
}
