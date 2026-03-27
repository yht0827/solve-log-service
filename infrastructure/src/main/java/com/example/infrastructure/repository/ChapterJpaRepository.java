package com.example.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.entity.Chapter;
import com.example.domain.repository.ChapterRepository;

public interface ChapterJpaRepository extends JpaRepository<Chapter, Long>, ChapterRepository {
}
