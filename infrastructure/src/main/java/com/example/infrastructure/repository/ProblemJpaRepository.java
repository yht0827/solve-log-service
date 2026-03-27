package com.example.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.entity.Problem;
import com.example.domain.repository.ProblemRepository;

public interface ProblemJpaRepository extends JpaRepository<Problem, Long>, ProblemRepository {
	List<Problem> findByChapterId(Long chapterId);
}
