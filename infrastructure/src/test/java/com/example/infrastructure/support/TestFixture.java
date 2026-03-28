package com.example.infrastructure.support;

import org.springframework.jdbc.core.JdbcTemplate;

public class TestFixture {

	public static void insertChapter(JdbcTemplate jdbc, long id, String name) {
		jdbc.update(
			"INSERT INTO chapter (id, name, created_at, updated_at) VALUES (?, ?, NOW(6), NOW(6)) ON DUPLICATE KEY UPDATE name=name",
			id, name);
	}

	public static void insertProblem(JdbcTemplate jdbc, long id, long chapterId) {
		jdbc.update(
			"INSERT INTO problem (id, chapter_id, content, answer_type, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(6), NOW(6)) ON DUPLICATE KEY UPDATE content=content",
			id, chapterId, "테스트 문제", "MULTIPLE_CHOICE");
	}

	public static void insertUser(JdbcTemplate jdbc, long id) {
		jdbc.update(
			"INSERT INTO users (id, name, created_at, updated_at) VALUES (?, ?, NOW(6), NOW(6)) ON DUPLICATE KEY UPDATE name=name",
			id, "testUser" + id);
	}

	public static void insertSolveLog(JdbcTemplate jdbc, long userId, long problemId, String status) {
		jdbc.update(
			"INSERT INTO problem_solve_log (user_id, problem_id, answer_status, created_at, updated_at) VALUES (?, ?, ?, NOW(6), NOW(6))",
			userId, problemId, status);
	}
}
