package com.example.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.port.out.ProblemSolveLogRepository;
import com.example.domain.enums.AnswerStatus;
import com.example.infrastructure.support.RepositoryTestSupport;
import com.example.infrastructure.support.TestFixture;

@Transactional
class ProblemSolveLogRepositoryImplTest extends RepositoryTestSupport {

	@Autowired
	ProblemSolveLogRepository problemSolveLogRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	// 테스트용 ID
	private static final long CHAPTER_ID = 100L;
	private static final long PROBLEM_ID = 100L;
	private static final long USER_ID = 1000L;

	@BeforeEach
	void setUp() {
		TestFixture.insertChapter(jdbcTemplate, CHAPTER_ID, "테스트 단원");
		TestFixture.insertProblem(jdbcTemplate, PROBLEM_ID, CHAPTER_ID);

		// 테스트 사용자 35명 + 풀이 이력 (24 CORRECT, 11 WRONG)
		for (int i = 0; i < 35; i++) {
			long userId = USER_ID + i;
			String status = i < 24 ? "CORRECT" : "WRONG";
			TestFixture.insertUser(jdbcTemplate, userId);
			TestFixture.insertSolveLog(jdbcTemplate, userId, PROBLEM_ID, status);
		}
	}

	@Test
	@DisplayName("사용자가 해당 단원에서 풀었던 문제 ID 목록을 반환한다")
	void findSolvedProblemIdsByUserIdAndChapterId() {
		List<Long> solvedIds = problemSolveLogRepository.findSolvedProblemIdsByUserIdAndChapterId(USER_ID, CHAPTER_ID);

		assertThat(solvedIds).containsExactlyInAnyOrder(PROBLEM_ID);
	}

	@Test
	@DisplayName("이미 풀이한 문제는 true를 반환한다")
	void existsByUserIdAndProblemId_solved() {
		assertThat(problemSolveLogRepository.existsByUserIdAndProblemId(USER_ID, PROBLEM_ID)).isTrue();
	}

	@Test
	@DisplayName("풀이하지 않은 문제는 false를 반환한다")
	void existsByUserIdAndProblemId_notSolved() {
		assertThat(problemSolveLogRepository.existsByUserIdAndProblemId(USER_ID, PROBLEM_ID + 1)).isFalse();
	}

	@Test
	@DisplayName("문제를 푼 사용자 수(중복 제거)를 반환한다")
	void countDistinctUsersByProblemId() {
		assertThat(problemSolveLogRepository.countDistinctUsersByProblemId(PROBLEM_ID)).isEqualTo(35);
	}

	@Test
	@DisplayName("특정 정답 상태의 풀이 수를 반환한다")
	void countByProblemIdAndAnswerStatus() {
		assertThat(
			problemSolveLogRepository.countByProblemIdAndAnswerStatus(PROBLEM_ID, AnswerStatus.CORRECT)).isEqualTo(24);
		assertThat(
			problemSolveLogRepository.countByProblemIdAndAnswerStatus(PROBLEM_ID, AnswerStatus.WRONG)).isEqualTo(11);
	}
}
