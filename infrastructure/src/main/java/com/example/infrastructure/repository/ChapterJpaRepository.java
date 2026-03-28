package com.example.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.entity.Chapter;

public interface ChapterJpaRepository extends JpaRepository<Chapter, Long> {
}
