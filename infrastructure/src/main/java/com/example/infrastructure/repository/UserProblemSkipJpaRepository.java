package com.example.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.entity.UserProblemSkip;
import com.example.domain.repository.UserProblemSkipRepository;

public interface UserProblemSkipJpaRepository extends JpaRepository<UserProblemSkip, Long>, UserProblemSkipRepository {
	Optional<UserProblemSkip> findByUserIdAndChapterId(Long userId, Long chapterId);
}
