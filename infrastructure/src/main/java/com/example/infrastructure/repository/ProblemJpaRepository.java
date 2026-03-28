package com.example.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.entity.Problem;

interface ProblemJpaRepository extends JpaRepository<Problem, Long> {
	List<Problem> findByChapterId(Long chapterId);
}
