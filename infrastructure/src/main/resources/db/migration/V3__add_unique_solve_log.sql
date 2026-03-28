ALTER TABLE problem_solve_log
    ADD CONSTRAINT uq_solve_log_user_problem UNIQUE (user_id, problem_id);
