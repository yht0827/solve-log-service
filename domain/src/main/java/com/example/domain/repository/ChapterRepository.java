package com.example.domain.repository;

import java.util.Optional;

import com.example.domain.entity.Chapter;

public interface ChapterRepository {
	Optional<Chapter> findById(Long id);
}
