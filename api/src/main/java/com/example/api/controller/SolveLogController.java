package com.example.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.response.SolveDetailResponse;
import com.example.application.port.in.GetSolveDetailUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@Tag(name = "SolveLog", description = "풀이 이력 API")
@Validated
@RestController
@RequestMapping("/api/v1/solve-logs")
@RequiredArgsConstructor
public class SolveLogController {

	private final GetSolveDetailUseCase getSolveDetailUseCase;

	@Operation(summary = "풀이 상세 조회", description = "사용자가 풀었던 문제의 상세 정보를 조회합니다.")
	@GetMapping("/{problemId}")
	public ResponseEntity<SolveDetailResponse> getSolveDetail(
		@PathVariable @Positive(message = "problemId는 양수여야 합니다.") Long problemId,
		@RequestParam @Positive(message = "userId는 양수여야 합니다.") Long userId) {
		return ResponseEntity.ok(SolveDetailResponse.from(
			getSolveDetailUseCase.getSolveDetail(userId, problemId)
		));
	}
}
