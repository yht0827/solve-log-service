package com.example.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.domain.entity.ProblemSolveLog;
import com.example.domain.enums.AnswerStatus;

interface ProblemSolveLogJpaRepository extends JpaRepository<ProblemSolveLog, Long> {

	@Query("SELECT psl.problemId FROM ProblemSolveLog psl WHERE psl.userId = :userId")
	List<Long> findSolvedProblemIdsByUserId(@Param("userId") Long userId);

	Optional<ProblemSolveLog> findByUserIdAndProblemId(Long userId, Long problemId);

	@Query("SELECT COUNT(DISTINCT psl.userId) FROM ProblemSolveLog psl WHERE psl.problemId = :problemId")
	long countDistinctUsersByProblemId(@Param("problemId") Long problemId);

	@Query("SELECT COUNT(psl) FROM ProblemSolveLog psl WHERE psl.problemId = :problemId AND psl.answerStatus = :answerStatus")
	long countByProblemIdAndAnswerStatus(@Param("problemId") Long problemId,
		@Param("answerStatus") AnswerStatus answerStatus);
}
