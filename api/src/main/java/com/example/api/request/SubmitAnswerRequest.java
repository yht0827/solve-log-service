package com.example.api.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SubmitAnswerRequest(
	@NotNull(message = "problemId는 필수입니다.")
	@Positive(message = "problemId는 양수여야 합니다.")
	Long problemId,

	@NotEmpty(message = "userAnswers는 비어있을 수 없습니다.")
	List<@NotBlank(message = "답변은 공백일 수 없습니다.") @Size(max = 100, message = "답변은 100자를 초과할 수 없습니다.") String> userAnswers
) {
}
