package com.example.application.port.out;

import java.util.Optional;

import com.example.domain.entity.Chapter;

public interface ChapterRepository {
	Optional<Chapter> findById(Long id);
}
