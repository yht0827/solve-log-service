package com.example.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.application.port.out.ProblemSolveLogRepository;
import com.example.domain.entity.ProblemSolveLog;
import com.example.domain.enums.AnswerStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProblemSolveLogRepositoryImpl implements ProblemSolveLogRepository {

	private final ProblemSolveLogJpaRepository jpaRepository;

	@Override
	public ProblemSolveLog save(ProblemSolveLog solveLog) {
		return jpaRepository.save(solveLog);
	}

	@Override
	public List<Long> findSolvedProblemIdsByUserId(Long userId) {
		return jpaRepository.findSolvedProblemIdsByUserId(userId);
	}

	@Override
	public Optional<ProblemSolveLog> findByUserIdAndProblemId(Long userId, Long problemId) {
		return jpaRepository.findByUserIdAndProblemId(userId, problemId);
	}

	@Override
	public long countDistinctUsersByProblemId(long problemId) {
		return jpaRepository.countDistinctUsersByProblemId(problemId);
	}

	@Override
	public long countByProblemIdAndAnswerStatus(long problemId, AnswerStatus answerStatus) {
		return jpaRepository.countByProblemIdAndAnswerStatus(problemId, answerStatus);
	}
}
