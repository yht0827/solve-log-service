package com.example.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.dto.SolveDetailResult;
import com.example.application.port.in.GetSolveDetailUseCase;
import com.example.application.port.out.ProblemRepository;
import com.example.application.port.out.ProblemSolveLogRepository;
import com.example.domain.entity.Problem;
import com.example.domain.entity.ProblemSolveLog;
import com.example.domain.enums.AnswerStatus;
import com.example.domain.exception.ProblemNotFoundException;
import com.example.domain.exception.SolveLogNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolveLogQueryService implements GetSolveDetailUseCase {

	private final ProblemRepository problemRepository;
	private final ProblemSolveLogRepository problemSolveLogRepository;

	@Transactional(readOnly = true)
	public SolveDetailResult getSolveDetail(Long userId, Long problemId) {
		Problem problem = problemRepository.findById(problemId)
			.orElseThrow(ProblemNotFoundException::new);

		ProblemSolveLog solveLog = problemSolveLogRepository.findByUserIdAndProblemId(userId, problemId)
			.orElseThrow(SolveLogNotFoundException::new);

		long totalSolvers = problemSolveLogRepository.countDistinctUsersByProblemId(problemId);
		long correctCount = problemSolveLogRepository.countByProblemIdAndAnswerStatus(problemId, AnswerStatus.CORRECT);
		Integer correctRate = Problem.correctRate(totalSolvers, correctCount);

		return SolveDetailResult.of(problem, solveLog, correctRate);
	}
}
