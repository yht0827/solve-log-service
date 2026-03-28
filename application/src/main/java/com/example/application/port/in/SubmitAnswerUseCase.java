package com.example.application.port.in;

import java.util.List;

import com.example.application.dto.SubmitResult;

public interface SubmitAnswerUseCase {

	SubmitResult submitAnswer(Long problemId, Long userId, List<String> userAnswers);
}
