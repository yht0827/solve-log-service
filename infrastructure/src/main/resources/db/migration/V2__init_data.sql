-- 챕터
INSERT INTO chapter (name, created_at, updated_at)
VALUES ('한국사 기초', NOW(), NOW()),
       ('세계 지리', NOW(), NOW());

-- 문제
INSERT INTO problem (chapter_id, content, answer_type, explanation, created_at, updated_at)
VALUES (1, '다음 중 조선 시대 왕이 아닌 인물은?', 'MULTIPLE_CHOICE',
        '광개토대왕은 고구려의 왕으로 조선 시대 왕이 아닙니다.', NOW(), NOW()),
       (1, '다음 중 대한민국 독립운동가를 모두 고르시오.', 'MULTIPLE_CHOICE',
        '유관순, 안중근, 윤봉길은 일제강점기 대표적인 독립운동가입니다. 이토 히로부미는 일본 정치인, 맥아더는 미국 군인입니다.', NOW(), NOW()),
       (1, '3·1 운동이 일어난 연도를 쓰시오.', 'SUBJECTIVE',
        '3·1 운동은 1919년 3월 1일 전국적으로 일어난 독립운동입니다.', NOW(), NOW()),
       (2, '다음 중 유럽에 속하는 나라는?', 'MULTIPLE_CHOICE',
        '프랑스는 서유럽에 위치한 나라입니다. 이집트는 아프리카, 인도는 아시아, 브라질은 남아메리카, 캐나다는 북아메리카에 속합니다.', NOW(), NOW()),
       (2, '세계에서 가장 넓은 나라는 어디인가?', 'SUBJECTIVE',
        '러시아는 약 1,710만 km²로 세계에서 가장 넓은 나라입니다.', NOW(), NOW()),
       (2, '다음 중 섬나라를 모두 고르시오.', 'MULTIPLE_CHOICE',
        '일본, 뉴질랜드, 영국은 섬나라입니다. 중국과 러시아는 대륙에 속하는 나라입니다.', NOW(), NOW());

-- 선택지 (문제 1: 조선 시대 왕이 아닌 인물)
INSERT INTO choice (problem_id, sequence, content)
VALUES (1, 1, '세종대왕'),
       (1, 2, '광개토대왕'),
       (1, 3, '태조'),
       (1, 4, '영조'),
       (1, 5, '정조');

-- 선택지 (문제 2: 독립운동가)
INSERT INTO choice (problem_id, sequence, content)
VALUES (2, 1, '유관순'),
       (2, 2, '안중근'),
       (2, 3, '이토 히로부미'),
       (2, 4, '윤봉길'),
       (2, 5, '맥아더');

-- 선택지 (문제 4: 유럽 나라)
INSERT INTO choice (problem_id, sequence, content)
VALUES (4, 1, '이집트'),
       (4, 2, '프랑스'),
       (4, 3, '인도'),
       (4, 4, '브라질'),
       (4, 5, '캐나다');

-- 선택지 (문제 6: 섬나라)
INSERT INTO choice (problem_id, sequence, content)
VALUES (6, 1, '중국'),
       (6, 2, '일본'),
       (6, 3, '뉴질랜드'),
       (6, 4, '러시아'),
       (6, 5, '영국');

-- 정답
INSERT INTO problem_answer (problem_id, answer_value)
VALUES (1, '2'),
       (2, '1'),
       (2, '2'),
       (2, '4'),
       (3, '1919'),
       (4, '2'),
       (5, '러시아'),
       (6, '2'),
       (6, '3'),
       (6, '5');

-- 사용자 (35명 - 정답률 계산 테스트용)
INSERT INTO users (name, created_at, updated_at)
VALUES ('user1', NOW(), NOW()),
       ('user2', NOW(), NOW()),
       ('user3', NOW(), NOW()),
       ('user4', NOW(), NOW()),
       ('user5', NOW(), NOW()),
       ('user6', NOW(), NOW()),
       ('user7', NOW(), NOW()),
       ('user8', NOW(), NOW()),
       ('user9', NOW(), NOW()),
       ('user10', NOW(), NOW()),
       ('user11', NOW(), NOW()),
       ('user12', NOW(), NOW()),
       ('user13', NOW(), NOW()),
       ('user14', NOW(), NOW()),
       ('user15', NOW(), NOW()),
       ('user16', NOW(), NOW()),
       ('user17', NOW(), NOW()),
       ('user18', NOW(), NOW()),
       ('user19', NOW(), NOW()),
       ('user20', NOW(), NOW()),
       ('user21', NOW(), NOW()),
       ('user22', NOW(), NOW()),
       ('user23', NOW(), NOW()),
       ('user24', NOW(), NOW()),
       ('user25', NOW(), NOW()),
       ('user26', NOW(), NOW()),
       ('user27', NOW(), NOW()),
       ('user28', NOW(), NOW()),
       ('user29', NOW(), NOW()),
       ('user30', NOW(), NOW()),
       ('user31', NOW(), NOW()),
       ('user32', NOW(), NOW()),
       ('user33', NOW(), NOW()),
       ('user34', NOW(), NOW()),
       ('user35', NOW(), NOW());

-- 풀이 이력 (문제1, 35명, 24명 정답 ≈ 69%)
INSERT INTO problem_solve_log (user_id, problem_id, answer_status, created_at, updated_at)
VALUES (1, 1, 'CORRECT', NOW(), NOW()),
       (2, 1, 'CORRECT', NOW(), NOW()),
       (3, 1, 'CORRECT', NOW(), NOW()),
       (4, 1, 'CORRECT', NOW(), NOW()),
       (5, 1, 'CORRECT', NOW(), NOW()),
       (6, 1, 'CORRECT', NOW(), NOW()),
       (7, 1, 'CORRECT', NOW(), NOW()),
       (8, 1, 'CORRECT', NOW(), NOW()),
       (9, 1, 'CORRECT', NOW(), NOW()),
       (10, 1, 'CORRECT', NOW(), NOW()),
       (11, 1, 'CORRECT', NOW(), NOW()),
       (12, 1, 'CORRECT', NOW(), NOW()),
       (13, 1, 'CORRECT', NOW(), NOW()),
       (14, 1, 'CORRECT', NOW(), NOW()),
       (15, 1, 'CORRECT', NOW(), NOW()),
       (16, 1, 'CORRECT', NOW(), NOW()),
       (17, 1, 'CORRECT', NOW(), NOW()),
       (18, 1, 'CORRECT', NOW(), NOW()),
       (19, 1, 'CORRECT', NOW(), NOW()),
       (20, 1, 'CORRECT', NOW(), NOW()),
       (21, 1, 'CORRECT', NOW(), NOW()),
       (22, 1, 'CORRECT', NOW(), NOW()),
       (23, 1, 'CORRECT', NOW(), NOW()),
       (24, 1, 'CORRECT', NOW(), NOW()),
       (25, 1, 'WRONG', NOW(), NOW()),
       (26, 1, 'WRONG', NOW(), NOW()),
       (27, 1, 'WRONG', NOW(), NOW()),
       (28, 1, 'WRONG', NOW(), NOW()),
       (29, 1, 'WRONG', NOW(), NOW()),
       (30, 1, 'WRONG', NOW(), NOW()),
       (31, 1, 'WRONG', NOW(), NOW()),
       (32, 1, 'WRONG', NOW(), NOW()),
       (33, 1, 'WRONG', NOW(), NOW()),
       (34, 1, 'WRONG', NOW(), NOW()),
       (35, 1, 'WRONG', NOW(), NOW());

-- 사용자 답변 (문제1 정답: 2번 광개토대왕)
INSERT INTO user_answer (solve_log_id, answer_value)
VALUES (1, '2'),
       (2, '2'),
       (3, '2'),
       (4, '2'),
       (5, '2'),
       (6, '2'),
       (7, '2'),
       (8, '2'),
       (9, '2'),
       (10, '2'),
       (11, '2'),
       (12, '2'),
       (13, '2'),
       (14, '2'),
       (15, '2'),
       (16, '2'),
       (17, '2'),
       (18, '2'),
       (19, '2'),
       (20, '2'),
       (21, '2'),
       (22, '2'),
       (23, '2'),
       (24, '2'),
       (25, '1'),
       (26, '3'),
       (27, '4'),
       (28, '5'),
       (29, '1'),
       (30, '3'),
       (31, '3'),
       (32, '4'),
       (33, '5'),
       (34, '1'),
       (35, '4');
