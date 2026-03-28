package com.example.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.entity.UserProblemSkip;

public interface UserProblemSkipJpaRepository extends JpaRepository<UserProblemSkip, Long> {
	Optional<UserProblemSkip> findByUserIdAndChapterId(Long userId, Long chapterId);
}
