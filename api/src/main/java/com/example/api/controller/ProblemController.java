package com.example.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.request.RandomProblemRequest;
import com.example.api.request.SubmitAnswerRequest;
import com.example.api.response.RandomProblemResponse;
import com.example.api.response.SubmitAnswerResponse;
import com.example.application.port.in.GetRandomProblemUseCase;
import com.example.application.port.in.SubmitAnswerUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Problem", description = "문제 API")
@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {

	private final GetRandomProblemUseCase getRandomProblemUseCase;
	private final SubmitAnswerUseCase submitAnswerUseCase;

	@Operation(summary = "랜덤 문제 조회", description = "단원별 랜덤 문제를 조회합니다. skipProblemId 포함 시 해당 문제를 건너뜁니다.")
	@PostMapping("/random")
	public ResponseEntity<RandomProblemResponse> getRandomProblem(@Valid @RequestBody RandomProblemRequest request) {
		return ResponseEntity.ok(RandomProblemResponse.from(
			getRandomProblemUseCase.getRandomProblem(request.chapterId(), request.userId(), request.skipProblemId())
		));
	}

	@Operation(summary = "문제 제출", description = "문제의 답안을 제출합니다.")
	@PostMapping("/submit")
	public ResponseEntity<SubmitAnswerResponse> submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {
		return ResponseEntity.ok(SubmitAnswerResponse.from(
			submitAnswerUseCase.submitAnswer(request.problemId(), request.userId(), request.userAnswers())
		));
	}
}
