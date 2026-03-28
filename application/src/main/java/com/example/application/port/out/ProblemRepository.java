package com.example.application.port.out;

import java.util.List;
import java.util.Optional;

import com.example.domain.entity.Problem;

public interface ProblemRepository {
	List<Problem> findByChapterId(Long chapterId);

	Optional<Problem> findById(Long id);
}
