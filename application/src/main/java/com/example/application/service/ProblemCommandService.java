package com.example.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.dto.SubmitResult;
import com.example.application.port.in.SubmitAnswerUseCase;
import com.example.application.port.out.ProblemRepository;
import com.example.application.port.out.ProblemSolveLogRepository;
import com.example.domain.entity.Problem;
import com.example.domain.entity.ProblemSolveLog;
import com.example.domain.entity.UserAnswer;
import com.example.domain.enums.AnswerStatus;
import com.example.domain.exception.ProblemNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemCommandService implements SubmitAnswerUseCase {

	private final ProblemRepository problemRepository;
	private final ProblemSolveLogRepository problemSolveLogRepository;

	@Transactional
	public SubmitResult submitAnswer(Long problemId, Long userId, List<String> userAnswers) {
		Problem problem = problemRepository.findById(problemId)
			.orElseThrow(ProblemNotFoundException::new);

		AnswerStatus answerStatus = problem.judge(userAnswers);

		ProblemSolveLog solveLog = ProblemSolveLog.create(userId, problemId, answerStatus);
		userAnswers.forEach(answer -> solveLog.addUserAnswer(UserAnswer.create(solveLog, answer)));
		problemSolveLogRepository.save(solveLog);

		return SubmitResult.of(problem, answerStatus);
	}
}
