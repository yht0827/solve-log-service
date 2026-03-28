package com.example.application.port.in;

import com.example.application.dto.SolveDetailResult;

public interface GetSolveDetailUseCase {

	SolveDetailResult getSolveDetail(Long userId, Long problemId);
}
