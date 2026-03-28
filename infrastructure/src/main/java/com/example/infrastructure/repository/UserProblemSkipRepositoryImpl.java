package com.example.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.application.port.out.UserProblemSkipRepository;
import com.example.domain.entity.UserProblemSkip;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserProblemSkipRepositoryImpl implements UserProblemSkipRepository {

	private final UserProblemSkipJpaRepository jpaRepository;

	@Override
	public Optional<UserProblemSkip> findByUserIdAndChapterId(Long userId, Long chapterId) {
		return jpaRepository.findByUserIdAndChapterId(userId, chapterId);
	}

	@Override
	public UserProblemSkip save(UserProblemSkip skip) {
		return jpaRepository.save(skip);
	}
}
