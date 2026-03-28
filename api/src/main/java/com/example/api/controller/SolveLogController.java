package com.example.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.auth.LoginUserId;
import com.example.api.request.SolveDetailRequest;
import com.example.api.response.SolveDetailResponse;
import com.example.application.port.in.GetSolveDetailUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "SolveLog", description = "풀이 이력 API")
@RestController
@RequestMapping("/api/v1/solve-logs")
@RequiredArgsConstructor
public class SolveLogController {

	private final GetSolveDetailUseCase getSolveDetailUseCase;

	@Operation(summary = "풀이 상세 조회", description = "사용자가 풀었던 문제의 상세 정보를 조회합니다.")
	@GetMapping
	public ResponseEntity<SolveDetailResponse> getSolveDetail(
		@LoginUserId Long userId,
		@ModelAttribute @Valid SolveDetailRequest request
	) {
		return ResponseEntity.ok(SolveDetailResponse.from(
			getSolveDetailUseCase.getSolveDetail(userId, request.problemId())
		));
	}
}
