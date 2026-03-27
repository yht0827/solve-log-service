CREATE TABLE chapter
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL
);

CREATE TABLE problem
(
    id          BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    chapter_id  BIGINT      NOT NULL,
    content     TEXT        NOT NULL,
    answer_type VARCHAR(50) NOT NULL,
    explanation TEXT,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    CONSTRAINT fk_problem_chapter FOREIGN KEY (chapter_id) REFERENCES chapter (id)
);

CREATE INDEX idx_problem_chapter_id ON problem (chapter_id);

CREATE TABLE choice
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT       NOT NULL,
    sequence   INT          NOT NULL,
    content    VARCHAR(500) NOT NULL,
    CONSTRAINT fk_choice_problem FOREIGN KEY (problem_id) REFERENCES problem (id)
);

CREATE TABLE problem_answer
(
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    problem_id   BIGINT       NOT NULL,
    answer_value VARCHAR(500) NOT NULL,
    CONSTRAINT fk_problem_answer_problem FOREIGN KEY (problem_id) REFERENCES problem (id)
);

CREATE TABLE users
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL
);

CREATE TABLE problem_solve_log
(
    id            BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT      NOT NULL,
    problem_id    BIGINT      NOT NULL,
    answer_status VARCHAR(20) NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    CONSTRAINT fk_solve_log_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_solve_log_problem FOREIGN KEY (problem_id) REFERENCES problem (id)
);

CREATE INDEX idx_solve_log_user_id ON problem_solve_log (user_id);
CREATE INDEX idx_solve_log_problem_id ON problem_solve_log (problem_id);
CREATE INDEX idx_solve_log_user_problem ON problem_solve_log (user_id, problem_id);

CREATE TABLE user_answer
(
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    solve_log_id BIGINT       NOT NULL,
    answer_value VARCHAR(500) NOT NULL,
    CONSTRAINT fk_user_answer_solve_log FOREIGN KEY (solve_log_id) REFERENCES problem_solve_log (id)
);

CREATE TABLE user_problem_skip
(
    id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    chapter_id BIGINT      NOT NULL,
    problem_id BIGINT      NOT NULL,
    skipped_at DATETIME(6) NOT NULL,
    CONSTRAINT uq_skip_user_chapter UNIQUE (user_id, chapter_id),
    CONSTRAINT fk_skip_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_skip_chapter FOREIGN KEY (chapter_id) REFERENCES chapter (id)
);
