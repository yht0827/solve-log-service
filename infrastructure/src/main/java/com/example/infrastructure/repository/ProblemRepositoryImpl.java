package com.example.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.application.port.out.ProblemRepository;
import com.example.domain.entity.Problem;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProblemRepositoryImpl implements ProblemRepository {

	private final ProblemJpaRepository jpaRepository;

	@Override
	public List<Problem> findByChapterId(Long chapterId) {
		return jpaRepository.findByChapterId(chapterId);
	}

	@Override
	public Optional<Problem> findById(Long id) {
		return jpaRepository.findById(id);
	}
}
