package com.example.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.application.dto.ProblemQueryResult;
import com.example.application.dto.SubmitResult;
import com.example.application.port.in.GetRandomProblemUseCase;
import com.example.application.port.in.ProblemCommandUseCase;
import com.example.domain.enums.AnswerStatus;
import com.example.domain.enums.AnswerType;
import com.example.domain.exception.AlreadySolvedException;

@WebMvcTest(ProblemController.class)
public class ProblemControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	GetRandomProblemUseCase getRandomProblemUseCase;
	@MockitoBean
	ProblemCommandUseCase problemCommandUseCase;

	@Nested
	@DisplayName("GET /api/v1/problems/random")
	class GetRandomProblemTest {

		@Test
		@DisplayName("정상 응답 200")
		void returns_200_with_problem() throws Exception {
			// given
			ProblemQueryResult result = new ProblemQueryResult(1L, "문제 내용", AnswerType.MULTIPLE_CHOICE.name(),
				List.of(new ProblemQueryResult.ChoiceInfo(1, "보기1"), new ProblemQueryResult.ChoiceInfo(2, "보기2")),
				75);
			given(getRandomProblemUseCase.getRandomProblem(1L, 1L)).willReturn(result);

			// when & then
			mockMvc.perform(get("/api/v1/problems/random")
					.param("chapterId", "1")
					.header("X-User-Id", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.problemId").value(1))
				.andExpect(jsonPath("$.answerType").value("MULTIPLE_CHOICE"))
				.andExpect(jsonPath("$.answerCorrectRate").value(75));
		}

		@Test
		@DisplayName("X-User-Id 헤더 없으면 401")
		void returns_401_when_header_missing() throws Exception {
			mockMvc.perform(get("/api/v1/problems/random")
					.param("chapterId", "1"))
				.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("chapterId 없으면 400")
		void returns_400_when_chapterId_missing() throws Exception {
			mockMvc.perform(get("/api/v1/problems/random")
					.header("X-User-Id", "1"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_INPUT"));
		}
	}

	@Nested
	@DisplayName("POST /api/v1/problems/skip")
	class SkipProblemTest {

		@Test
		@DisplayName("정상 응답 204")
		void returns_204() throws Exception {
			// given
			willDoNothing().given(problemCommandUseCase).skipProblem(anyLong(), anyLong(), anyLong());

			// when & then
			mockMvc.perform(post("/api/v1/problems/skip")
					.contentType(MediaType.APPLICATION_JSON)
					.header("X-User-Id", "1")
					.content("{\"problemId\":1,\"chapterId\":1}"))
				.andExpect(status().isNoContent());
		}

		@Test
		@DisplayName("X-User-Id 헤더 없으면 401")
		void returns_401_when_header_missing() throws Exception {
			mockMvc.perform(post("/api/v1/problems/skip")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"problemId\":1,\"chapterId\":1}"))
				.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("problemId 없으면 400")
		void returns_400_when_problemId_null() throws Exception {
			mockMvc.perform(post("/api/v1/problems/skip")
					.contentType(MediaType.APPLICATION_JSON)
					.header("X-User-Id", "1")
					.content("{\"chapterId\":1}"))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("POST /api/v1/problems/submit")
	class SubmitAnswerTest {

		@Test
		@DisplayName("정상 응답 200")
		void returns_200_with_submit_result() throws Exception {
			// given
			SubmitResult result = new SubmitResult(1L, AnswerStatus.CORRECT, "해설", List.of("1"));
			given(problemCommandUseCase.submitAnswer(1L, 1L, List.of("1"))).willReturn(result);

			// when & then
			mockMvc.perform(post("/api/v1/problems/submit")
					.contentType(MediaType.APPLICATION_JSON)
					.header("X-User-Id", "1")
					.content("{\"problemId\":1,\"userAnswers\":[\"1\"]}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.answerStatus").value("CORRECT"));
		}

		@Test
		@DisplayName("X-User-Id 헤더 없으면 401")
		void returns_401_when_header_missing() throws Exception {
			mockMvc.perform(post("/api/v1/problems/submit")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"problemId\":1,\"userAnswers\":[\"1\"]}"))
				.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("userAnswers 비어있으면 400")
		void returns_400_when_userAnswers_empty() throws Exception {
			mockMvc.perform(post("/api/v1/problems/submit")
					.contentType(MediaType.APPLICATION_JSON)
					.header("X-User-Id", "1")
					.content("{\"problemId\":1,\"userAnswers\":[]}"))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("이미 풀이한 문제면 409")
		void returns_409_when_already_solved() throws Exception {
			// given
			given(problemCommandUseCase.submitAnswer(1L, 1L, List.of("1")))
				.willThrow(new AlreadySolvedException());

			// when & then
			mockMvc.perform(post("/api/v1/problems/submit")
					.contentType(MediaType.APPLICATION_JSON)
					.header("X-User-Id", "1")
					.content("{\"problemId\":1,\"userAnswers\":[\"1\"]}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("ALREADY_SOLVED"));
		}

		@Test
		@DisplayName("답변이 공백이면 400")
		void returns_400_when_answer_is_blank() throws Exception {
			mockMvc.perform(post("/api/v1/problems/submit")
					.contentType(MediaType.APPLICATION_JSON)
					.header("X-User-Id", "1")
					.content("{\"problemId\":1,\"userAnswers\":[\"   \"]}"))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("답변이 100자를 초과하면 400")
		void returns_400_when_answer_exceeds_max_length() throws Exception {
			// given
			String longAnswer = "a".repeat(101);

			// when & then
			mockMvc.perform(post("/api/v1/problems/submit")
					.contentType(MediaType.APPLICATION_JSON)
					.header("X-User-Id", "1")
					.content("{\"problemId\":1,\"userAnswers\":[\"" + longAnswer + "\"]}"))
				.andExpect(status().isBadRequest());
		}

	}
}
