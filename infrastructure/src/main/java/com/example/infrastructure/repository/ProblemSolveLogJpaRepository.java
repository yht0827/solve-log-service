package com.example.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.domain.entity.ProblemSolveLog;
import com.example.domain.enums.AnswerStatus;

public interface ProblemSolveLogJpaRepository extends JpaRepository<ProblemSolveLog, Long> {

	@Query("SELECT DISTINCT psl.problemId FROM ProblemSolveLog psl WHERE psl.userId = :userId AND psl.problemId IN (SELECT p.id FROM Problem p WHERE p.chapterId = :chapterId)")
	List<Long> findSolvedProblemIdsByUserIdAndChapterId(@Param("userId") Long userId, @Param("chapterId") Long chapterId);

	boolean existsByUserIdAndProblemId(Long userId, Long problemId);

	@Query("SELECT psl FROM ProblemSolveLog psl LEFT JOIN FETCH psl.userAnswers WHERE psl.userId = :userId AND psl.problemId = :problemId")
	Optional<ProblemSolveLog> findByUserIdAndProblemId(@Param("userId") Long userId, @Param("problemId") Long problemId);

	@Query("SELECT COUNT(DISTINCT psl.userId) FROM ProblemSolveLog psl WHERE psl.problemId = :problemId")
	long countDistinctUsersByProblemId(@Param("problemId") Long problemId);

	@Query("SELECT COUNT(psl) FROM ProblemSolveLog psl WHERE psl.problemId = :problemId AND psl.answerStatus = :answerStatus")
	long countByProblemIdAndAnswerStatus(@Param("problemId") Long problemId,
		@Param("answerStatus") AnswerStatus answerStatus);
}
