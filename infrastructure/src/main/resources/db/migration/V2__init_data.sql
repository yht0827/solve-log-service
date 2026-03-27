-- 챕터
INSERT INTO chapter (name, created_at, updated_at)
VALUES ('수학 기초', NOW(), NOW()),
       ('물리 기초', NOW(), NOW());

-- 문제
INSERT INTO problem (chapter_id, content, answer_type, explanation, created_at, updated_at)
VALUES (1, '다음 중 2의 제곱(2²)은 무엇인가?', 'MULTIPLE_CHOICE',
        '2²는 2×2 = 4입니다.', NOW(), NOW()),
       (1, '다음 중 소수(Prime Number)를 모두 고르시오.', 'MULTIPLE_CHOICE',
        '소수는 1과 자기 자신만으로 나누어 떨어지는 1보다 큰 자연수입니다. 2, 3, 5는 소수이며 1과 4는 소수가 아닙니다.', NOW(), NOW()),
       (1, '피타고라스 정리를 수식으로 표현하시오. (빗변을 c로 표기)', 'SUBJECTIVE',
        '직각삼각형에서 a²+b²=c²이 성립합니다.', NOW(), NOW()),
       (2, '뉴턴의 운동 제2법칙(F=ma)에서 F, m, a가 각각 나타내는 것을 올바르게 짝지은 것은?', 'MULTIPLE_CHOICE',
        'F=ma에서 F는 힘(Force), m은 질량(Mass), a는 가속도(Acceleration)입니다.', NOW(), NOW()),
       (2, '빛의 속도는 약 몇 km/s인가?', 'SUBJECTIVE',
        '빛의 속도는 진공에서 약 300,000 km/s(정확히는 299,792 km/s)입니다.', NOW(), NOW()),
       (2, '다음 중 에너지 보존 법칙에 대한 설명으로 옳은 것은?', 'MULTIPLE_CHOICE',
        '에너지는 다른 형태로 변환될 수 있지만 생성되거나 소멸되지 않습니다.', NOW(), NOW());

-- 선택지 (문제 1)
INSERT INTO choice (problem_id, sequence, content)
VALUES (1, 1, '2'),
       (1, 2, '4'),
       (1, 3, '6'),
       (1, 4, '8'),
       (1, 5, '10');

-- 선택지 (문제 2)
INSERT INTO choice (problem_id, sequence, content)
VALUES (2, 1, '1'),
       (2, 2, '2'),
       (2, 3, '3'),
       (2, 4, '4'),
       (2, 5, '5');

-- 선택지 (문제 4)
INSERT INTO choice (problem_id, sequence, content)
VALUES (4, 1, 'F=힘, m=질량, a=가속도'),
       (4, 2, 'F=힘, m=속도, a=가속도'),
       (4, 3, 'F=질량, m=힘, a=가속도'),
       (4, 4, 'F=힘, m=질량, a=속력'),
       (4, 5, 'F=힘, m=무게, a=가속도');

-- 선택지 (문제 6)
INSERT INTO choice (problem_id, sequence, content)
VALUES (6, 1, '에너지는 소멸될 수 있다'),
       (6, 2, '에너지는 새로 생성될 수 있다'),
       (6, 3, '에너지는 형태가 변환되어도 총량은 일정하다'),
       (6, 4, '에너지는 마찰에 의해 감소한다'),
       (6, 5, '운동 에너지만 보존된다');

-- 정답
INSERT INTO problem_answer (problem_id, answer_value)
VALUES (1, '2'),
       (2, '2'),
       (2, '3'),
       (2, '5'),
       (3, 'a²+b²=c²'),
       (4, '1'),
       (5, '300000'),
       (6, '3');

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
       (31, 1, 'PARTIAL', NOW(), NOW()),
       (32, 1, 'PARTIAL', NOW(), NOW()),
       (33, 1, 'PARTIAL', NOW(), NOW()),
       (34, 1, 'WRONG', NOW(), NOW()),
       (35, 1, 'WRONG', NOW(), NOW());

-- 사용자 답변
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
       (29, '3'),
       (30, '1'),
       (31, '1'),
       (31, '2'),
       (32, '2'),
       (32, '3'),
       (33, '2'),
       (33, '4'),
       (34, '3'),
       (35, '5');
