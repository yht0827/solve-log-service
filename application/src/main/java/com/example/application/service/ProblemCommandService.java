package com.example.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.dto.SubmitResult;
import com.example.application.port.in.ProblemCommandUseCase;
import com.example.application.port.out.ProblemRepository;
import com.example.application.port.out.ProblemSolveLogRepository;
import com.example.application.port.out.UserProblemSkipRepository;
import com.example.domain.entity.Problem;
import com.example.domain.entity.ProblemSolveLog;
import com.example.domain.entity.UserAnswer;
import com.example.domain.entity.UserProblemSkip;
import com.example.domain.enums.AnswerStatus;
import com.example.domain.exception.ProblemNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemCommandService implements ProblemCommandUseCase {

	private final ProblemRepository problemRepository;
	private final ProblemSolveLogRepository problemSolveLogRepository;
	private final UserProblemSkipRepository userProblemSkipRepository;

	@Transactional
	public SubmitResult submitAnswer(Long problemId, Long userId, List<String> userAnswers) {
		// 문제 존재 확인
		Problem problem = problemRepository.findById(problemId)
			.orElseThrow(ProblemNotFoundException::new);

		// 정답 여부 판정
		AnswerStatus answerStatus = problem.judge(userAnswers);

		// 풀이 이력 저장
		ProblemSolveLog solveLog = ProblemSolveLog.create(userId, problemId, answerStatus);
		userAnswers.forEach(answer -> solveLog.addUserAnswer(UserAnswer.create(solveLog, answer)));
		problemSolveLogRepository.save(solveLog);

		return SubmitResult.of(problem, answerStatus);
	}

	@Transactional
	public void skipProblem(Long userId, Long chapterId, Long problemId) {
		// 기존 스킵 기록이 있으면 갱신, 없으면 신규 생성
		userProblemSkipRepository.findByUserIdAndChapterId(userId, chapterId)
			.ifPresentOrElse(
				skip -> {
					skip.updateProblemId(problemId);
					userProblemSkipRepository.save(skip);
				},
				() -> userProblemSkipRepository.save(UserProblemSkip.create(userId, chapterId, problemId))
			);
	}
}
