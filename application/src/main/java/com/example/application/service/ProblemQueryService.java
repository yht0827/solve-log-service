package com.example.application.service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.dto.ProblemQueryResult;
import com.example.application.port.in.GetRandomProblemUseCase;
import com.example.application.port.out.ChapterRepository;
import com.example.application.port.out.ProblemRepository;
import com.example.application.port.out.ProblemSolveLogRepository;
import com.example.application.port.out.UserProblemSkipRepository;
import com.example.domain.entity.Problem;
import com.example.domain.entity.UserProblemSkip;
import com.example.domain.enums.AnswerStatus;
import com.example.domain.exception.ChapterNotFoundException;
import com.example.domain.exception.NoAvailableProblemException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemQueryService implements GetRandomProblemUseCase {

	private final ChapterRepository chapterRepository;
	private final ProblemRepository problemRepository;
	private final ProblemSolveLogRepository problemSolveLogRepository;
	private final UserProblemSkipRepository userProblemSkipRepository;
	private final Random random = new Random();

	public ProblemQueryResult getRandomProblem(Long chapterId, Long userId) {
		// 단원 존재 확인
		chapterRepository.findById(chapterId).orElseThrow(ChapterNotFoundException::new);

		// 풀 수 있는 문제 중 랜덤 선택
		Problem selected = selectRandomAvailableProblem(chapterId, userId);

		// 정답률 계산
		Integer correctRate = resolveCorrectRate(selected.getId());

		return ProblemQueryResult.of(selected, correctRate);
	}

	private Problem selectRandomAvailableProblem(Long chapterId, Long userId) {
		// 단원 전체 문제 조회
		List<Problem> allProblems = problemRepository.findByChapterId(chapterId);

		// 이미 푼 문제 ID 목록 (현재 단원 내에서만 조회)
		Set<Long> solvedProblemIds = new HashSet<>(
			problemSolveLogRepository.findSolvedProblemIdsByUserIdAndChapterId(userId, chapterId));

		// 마지막으로 건너뛴 문제 ID
		Long lastSkippedProblemId = userProblemSkipRepository.findByUserIdAndChapterId(userId, chapterId)
			.map(UserProblemSkip::getProblemId)
			.orElse(null);

		// 풀었거나 건너뛴 문제 제외 후 랜덤 선택
		List<Problem> available = Problem.filterAvailable(allProblems, solvedProblemIds, lastSkippedProblemId);
		if (available.isEmpty()) {
			throw new NoAvailableProblemException();
		}

		return available.get(random.nextInt(available.size()));
	}

	private Integer resolveCorrectRate(Long problemId) {
		// 정답률 계산 (30명 이상 풀어야 노출)
		long totalSolvers = problemSolveLogRepository.countDistinctUsersByProblemId(problemId);
		long correctCount = problemSolveLogRepository.countByProblemIdAndAnswerStatus(problemId, AnswerStatus.CORRECT);
		return Problem.correctRate(totalSolvers, correctCount);
	}
}
