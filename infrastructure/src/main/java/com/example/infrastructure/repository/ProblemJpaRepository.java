package com.example.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.domain.entity.Problem;

public interface ProblemJpaRepository extends JpaRepository<Problem, Long> {

	@Query("SELECT DISTINCT p FROM Problem p LEFT JOIN FETCH p.choices LEFT JOIN FETCH p.answers WHERE p.id = :id")
	Optional<Problem> findById(@Param("id") Long id);

	@Query("SELECT DISTINCT p FROM Problem p LEFT JOIN FETCH p.choices LEFT JOIN FETCH p.answers WHERE p.chapterId = :chapterId")
	List<Problem> findByChapterId(@Param("chapterId") Long chapterId);
}
