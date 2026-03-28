package com.example.application.port.out;

import java.util.List;
import java.util.Optional;

import com.example.domain.entity.ProblemSolveLog;
import com.example.domain.enums.AnswerStatus;

public interface ProblemSolveLogRepository {

	ProblemSolveLog save(ProblemSolveLog solveLog);

	List<Long> findSolvedProblemIdsByUserId(Long userId);

	Optional<ProblemSolveLog> findByUserIdAndProblemId(Long userId, Long problemId);

	long countDistinctUsersByProblemId(long problemId);

	long countByProblemIdAndAnswerStatus(long problemId, AnswerStatus answerStatus);
}
