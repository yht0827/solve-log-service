package com.example.domain.repository;

import java.util.Optional;

import com.example.domain.entity.UserProblemSkip;

public interface UserProblemSkipRepository {
	Optional<UserProblemSkip> findByUserIdAndChapterId(Long userId, Long chapterId);

	UserProblemSkip save(UserProblemSkip skip);
}
