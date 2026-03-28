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
@RequiredArgsConstructor
public class ProblemQueryService implements GetRandomProblemUseCase {

	private final ChapterRepository chapterRepository;
	private final ProblemRepository problemRepository;
	private final ProblemSolveLogRepository problemSolveLogRepository;
	private final UserProblemSkipRepository userProblemSkipRepository;
	private final Random random = new Random();

	@Transactional(readOnly = true)
	public ProblemQueryResult getRandomProblem(Long chapterId, Long userId) {
		// 단원 존재 확인
		chapterRepository.findById(chapterId)
			.orElseThrow(ChapterNotFoundException::new);

		// 단원 전체 문제 조회
		List<Problem> allProblems = problemRepository.findByChapterId(chapterId);

		// 이미 푼 문제 ID 목록
		Set<Long> solvedProblemIds = new HashSet<>(problemSolveLogRepository.findSolvedProblemIdsByUserId(userId));

		// 마지막으로 건너뛴 문제 ID (없으면 null)
		Long lastSkippedProblemId = userProblemSkipRepository.findByUserIdAndChapterId(userId, chapterId)
			.map(UserProblemSkip::getProblemId)
			.orElse(null);

		// 풀었거나 건너뛴 문제 제외
		List<Problem> available = Problem.filterAvailable(allProblems, solvedProblemIds, lastSkippedProblemId);
		if (available.isEmpty()) {
			throw new NoAvailableProblemException();
		}

		// 랜덤 선택
		Problem selected = available.get(random.nextInt(available.size()));

		// 정답률 계산
		long totalSolvers = problemSolveLogRepository.countDistinctUsersByProblemId(selected.getId());
		long correctCount = problemSolveLogRepository.countByProblemIdAndAnswerStatus(selected.getId(),
			AnswerStatus.CORRECT);
		Integer correctRate = Problem.correctRate(totalSolvers, correctCount);

		return ProblemQueryResult.of(selected, correctRate);
	}
}
