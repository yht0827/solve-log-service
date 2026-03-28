package com.example.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SkipProblemRequest(
	@NotNull(message = "problemId는 필수입니다.")
	@Positive(message = "problemId는 양수여야 합니다.")
	Long problemId,

	@NotNull(message = "chapterId는 필수입니다.")
	@Positive(message = "chapterId는 양수여야 합니다.")
	Long chapterId
) {
}
