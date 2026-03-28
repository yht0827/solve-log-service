package com.example.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.application.port.out.ChapterRepository;
import com.example.domain.entity.Chapter;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChapterRepositoryImpl implements ChapterRepository {

	private final ChapterJpaRepository jpaRepository;

	@Override
	public Optional<Chapter> findById(Long id) {
		return jpaRepository.findById(id);
	}
}
