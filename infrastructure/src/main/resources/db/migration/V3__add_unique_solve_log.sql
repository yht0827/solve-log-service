-- unique 제약 추가 (중복 제출 방지, idx_solve_log_user_problem 인덱스 대체)
ALTER TABLE problem_solve_log
    ADD CONSTRAINT uq_solve_log_user_problem UNIQUE (user_id, problem_id);

-- 중복 인덱스 제거 (unique 제약이 동일한 역할을 함)
DROP INDEX idx_solve_log_user_problem ON problem_solve_log;

-- 정답률 집계 쿼리 최적화 (problem_id + answer_status 복합 인덱스)
CREATE INDEX idx_solve_log_problem_status ON problem_solve_log (problem_id, answer_status);
