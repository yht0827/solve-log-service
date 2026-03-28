package com.example.application.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

	@Transactional
	public ProblemQueryResult getRandomProblem(Long chapterId, Long userId, Long skipProblemId) {
		chapterRepository.findById(chapterId)
			.orElseThrow(ChapterNotFoundException::new);

		List<Problem> allProblems = problemRepository.findByChapterId(chapterId);

		Set<Long> solvedProblemIds = new HashSet<>(problemSolveLogRepository.findSolvedProblemIdsByUserId(userId));

		Optional<UserProblemSkip> lastSkip = userProblemSkipRepository.findByUserIdAndChapterId(userId, chapterId);
		Long lastSkippedProblemId = lastSkip.map(UserProblemSkip::getProblemId).orElse(null);

		if (skipProblemId != null) {
			recordSkip(userId, chapterId, skipProblemId, lastSkip);
			lastSkippedProblemId = skipProblemId;
		}

		List<Problem> available = Problem.filterAvailable(allProblems, solvedProblemIds, lastSkippedProblemId);
		if (available.isEmpty()) {
			throw new NoAvailableProblemException();
		}

		Problem selected = available.get(random.nextInt(available.size()));

		long totalSolvers = problemSolveLogRepository.countDistinctUsersByProblemId(selected.getId());
		long correctCount = problemSolveLogRepository.countByProblemIdAndAnswerStatus(selected.getId(), AnswerStatus.CORRECT);
		Integer correctRate = Problem.correctRate(totalSolvers, correctCount);

		return ProblemQueryResult.of(selected, correctRate);
	}

	private void recordSkip(Long userId, Long chapterId, Long skipProblemId, Optional<UserProblemSkip> existing) {
		if (existing.isPresent()) {
			existing.get().updateProblemId(skipProblemId);
			userProblemSkipRepository.save(existing.get());
		} else {
			userProblemSkipRepository.save(UserProblemSkip.create(userId, chapterId, skipProblemId));
		}
	}
}
