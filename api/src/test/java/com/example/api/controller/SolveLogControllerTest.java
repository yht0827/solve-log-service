package com.example.api.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.application.dto.SolveDetailResult;
import com.example.application.port.in.GetSolveDetailUseCase;
import com.example.domain.enums.AnswerStatus;

@WebMvcTest(SolveLogController.class)
public class SolveLogControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	GetSolveDetailUseCase getSolveDetailUseCase;

	@Nested
	@DisplayName("GET /api/v1/solve-logs")
	class GetSolveDetailTest {

		@Test
		@DisplayName("정상 응답 200")
		void returns_200_with_solve_detail() throws Exception {
			// given
			SolveDetailResult result = new SolveDetailResult(
				1L, AnswerStatus.CORRECT, "해설", List.of("1"), List.of("1"), 60);
			given(getSolveDetailUseCase.getSolveDetail(1L, 1L)).willReturn(result);

			// when & then
			mockMvc.perform(get("/api/v1/solve-logs")
					.param("problemId", "1")
					.header("X-User-Id", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.problemId").value(1))
				.andExpect(jsonPath("$.answerStatus").value("CORRECT"))
				.andExpect(jsonPath("$.answerCorrectRate").value(60));
		}

		@Test
		@DisplayName("X-User-Id 헤더 없으면 401")
		void returns_401_when_header_missing() throws Exception {
			mockMvc.perform(get("/api/v1/solve-logs")
					.param("problemId", "1"))
				.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("problemId 없으면 400")
		void returns_400_when_problemId_missing() throws Exception {
			mockMvc.perform(get("/api/v1/solve-logs")
					.header("X-User-Id", "1"))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("problemId가 음수면 400")
		void returns_400_when_problemId_negative() throws Exception {
			mockMvc.perform(get("/api/v1/solve-logs")
					.param("problemId", "-1")
					.header("X-User-Id", "1"))
				.andExpect(status().isBadRequest());
		}
	}
}
