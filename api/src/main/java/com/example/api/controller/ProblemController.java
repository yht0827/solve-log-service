package com.example.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.request.SkipProblemRequest;
import com.example.api.request.SubmitAnswerRequest;
import com.example.api.response.RandomProblemResponse;
import com.example.api.response.SubmitAnswerResponse;
import com.example.application.port.in.GetRandomProblemUseCase;
import com.example.application.port.in.ProblemCommandUseCase;

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
	private final ProblemCommandUseCase problemCommandUseCase;

	@Operation(summary = "랜덤 문제 조회", description = "단원별 랜덤 문제를 조회합니다.")
	@GetMapping("/random")
	public ResponseEntity<RandomProblemResponse> getRandomProblem(
		@RequestParam Long chapterId,
		@RequestParam Long userId
	) {
		return ResponseEntity.ok(RandomProblemResponse.from(
			getRandomProblemUseCase.getRandomProblem(chapterId, userId)
		));
	}

	@Operation(summary = "문제 건너뛰기", description = "현재 문제를 건너뜁니다.")
	@PostMapping("/{id}/skip")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void skipProblem(@PathVariable Long id, @Valid @RequestBody SkipProblemRequest request) {
		problemCommandUseCase.skipProblem(request.userId(), request.chapterId(), id);
	}

	@Operation(summary = "문제 제출", description = "문제의 답안을 제출합니다.")
	@PostMapping("/submit")
	public ResponseEntity<SubmitAnswerResponse> submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {
		return ResponseEntity.ok(SubmitAnswerResponse.from(
			problemCommandUseCase.submitAnswer(request.problemId(), request.userId(), request.userAnswers())
		));
	}
}
