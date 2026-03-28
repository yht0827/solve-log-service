package com.example.application.fixture;

import static org.instancio.Select.*;

import org.instancio.Instancio;

import com.example.domain.entity.Chapter;

public class ChapterFixture {

	public static Chapter withId(Long id) {
		return Instancio.of(Chapter.class)
			.set(field(Chapter.class, "id"), id)
			.create();
	}
}
