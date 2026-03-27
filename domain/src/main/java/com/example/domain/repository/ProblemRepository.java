package com.example.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.domain.entity.Problem;

public interface ProblemRepository {
	List<Problem> findByChapterId(Long chapterId);

	Optional<Problem> findById(Long id);
}
