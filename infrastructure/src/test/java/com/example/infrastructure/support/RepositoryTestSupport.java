package com.example.infrastructure.support;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class RepositoryTestSupport {

	static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("solve_log_db")
		.withUsername("root")
		.withPassword("password");

	@BeforeAll
	static void startContainer() {
		if (!mysql.isRunning()) {
			mysql.start();
		}
	}

	@DynamicPropertySource
	static void datasourceProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", () ->
			mysql.getJdbcUrl()
				+ "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul");
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}
}
