package com.example.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RandomProblemRequest(
	@NotNull(message = "chapterId는 필수입니다.")
	@Positive(message = "chapterId는 양수여야 합니다.")
	Long chapterId,

	@NotNull(message = "userId는 필수입니다.")
	@Positive(message = "userId는 양수여야 합니다.")
	Long userId,

	Long skipProblemId
) {
}
