package com.example.application.port.in;

import com.example.application.dto.ProblemQueryResult;

public interface GetRandomProblemUseCase {

	ProblemQueryResult getRandomProblem(Long chapterId, Long userId, Long skipProblemId);
}
