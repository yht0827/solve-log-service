package com.example.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.port.out.UserProblemSkipRepository;
import com.example.domain.entity.UserProblemSkip;
import com.example.infrastructure.support.RepositoryTestSupport;
import com.example.infrastructure.support.TestFixture;

@Transactional
class UserProblemSkipRepositoryImplTest extends RepositoryTestSupport {

	@Autowired
	UserProblemSkipRepository userProblemSkipRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		TestFixture.insertUser(jdbcTemplate, 101L);
		TestFixture.insertUser(jdbcTemplate, 102L);
		TestFixture.insertChapter(jdbcTemplate, 10L, "테스트 단원");
	}

	@Test
	@DisplayName("건너뛰기 이력이 없으면 empty를 반환한다")
	void findByUserIdAndChapterId_empty() {
		assertThat(userProblemSkipRepository.findByUserIdAndChapterId(101L, 10L)).isEmpty();
	}

	@Test
	@DisplayName("건너뛰기를 저장하면 조회할 수 있다")
	void save_andFind() {
		userProblemSkipRepository.save(UserProblemSkip.create(101L, 10L, 1L));

		assertThat(userProblemSkipRepository.findByUserIdAndChapterId(101L, 10L))
			.isPresent()
			.hasValueSatisfying(s -> assertThat(s.getProblemId()).isEqualTo(1L));
	}

	@Test
	@DisplayName("같은 사용자·단원의 건너뛰기는 갱신된다")
	void update_skip() {
		userProblemSkipRepository.save(UserProblemSkip.create(102L, 10L, 1L));

		UserProblemSkip skip = userProblemSkipRepository.findByUserIdAndChapterId(102L, 10L).orElseThrow();
		skip.updateProblemId(2L);
		userProblemSkipRepository.save(skip);

		assertThat(userProblemSkipRepository.findByUserIdAndChapterId(102L, 10L))
			.isPresent()
			.hasValueSatisfying(s -> assertThat(s.getProblemId()).isEqualTo(2L));
	}
}
