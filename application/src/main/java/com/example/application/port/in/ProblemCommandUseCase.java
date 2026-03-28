package com.example.application.port.in;

import java.util.List;

import com.example.application.dto.SubmitResult;

public interface ProblemCommandUseCase {

	SubmitResult submitAnswer(Long problemId, Long userId, List<String> userAnswers);

	void skipProblem(Long userId, Long chapterId, Long problemId);
}
