-- 従業員テーブルのダミーデータ挿入
INSERT INTO employees (employee_id, name, email, department, phone_number, date_of_birth)
VALUES
	(0, 'dash John', 'dashJohn@example.com', 'Sales', '555-0101', '1985-05-15'),
    (1, 'John Doe', 'john.doe@example.com', 'Sales', '555-0101', '1985-05-15'),
    (2, 'Jane Smith', 'jane.smith@example.com', 'Marketing', '555-0102', '1990-10-20');

-- 認証テーブルへのダミーデータの追加
-- password：adminpass
INSERT INTO authentications (employee_id, username, password, authority)
VALUES (0, 'admin', '$2a$10$4ws3NzLezXw7nxFY4MRdOuGEqpKEPRmAwRAMnJGvAUOqdagyLd376', 'ADMIN');
-- password：adminpass
INSERT INTO authentications (employee_id, username, password, authority)
VALUES (1, 'admin', '$2a$10$4ws3NzLezXw7nxFY4MRdOuGEqpKEPRmAwRAMnJGvAUOqdagyLd376', 'ADMIN');
-- password：userpass
INSERT INTO authentications (employee_id, username, password, authority)
VALUES (2, 'user', '$2a$10$/jar9xXQ6lrnVjLvLGv5BepFkLnGIO49RrGx42p2i.1hQt1BZ/7E2', 'USER');

-- 勤怠記録テーブルにデータを挿入
INSERT INTO attendance_records (record_id, employee_id, clock_in, clock_out) VALUES
(105, 0, '2024-01-02 08:00:00', '2024-01-02 17:00:00'),
(209, 0, '2024-01-03 08:15:00', '2024-01-03 17:10:00'),
(314, 0, '2024-01-04 08:05:00', '2024-01-04 16:55:00'),
(403, 0, '2024-01-05 08:10:00', '2024-01-05 17:20:00'),
(507, 0, '2024-01-06 08:00:00', '2024-01-06 17:00:00'),
(612, 0, '2024-02-01 09:00:00', '2024-02-01 18:00:00'),
(720, 0, '2024-02-02 09:05:00', '2024-02-02 18:10:00'),
(835, 0, '2024-02-03 09:10:00', '2024-02-03 18:05:00'),
(901, 0, '2024-02-04 09:00:00', '2024-02-04 18:15:00'),
(1002, 0, '2024-02-05 09:05:00', '2024-02-05 18:10:00'),
(1108, 0, '2024-03-01 08:00:00', '2024-03-01 17:00:00'),
(1213, 0, '2024-03-02 08:10:00', '2024-03-02 17:05:00'),
(1307, 0, '2024-03-03 08:05:00', '2024-03-03 17:10:00'),
(1405, 0, '2024-03-04 08:15:00', '2024-03-04 17:20:00'),
(1501, 0, '2024-03-05 08:00:00', '2024-03-05 17:00:00'),
(1609, 0, '2024-04-01 08:00:00', '2024-04-01 17:00:00'),
(1710, 0, '2024-04-02 08:20:00', '2024-04-02 17:10:00'),
(1804, 0, '2024-04-03 08:10:00', '2024-04-03 17:05:00'),
(1907, 0, '2024-04-04 08:15:00', '2024-04-04 17:20:00'),
(2006, 0, '2024-04-05 08:00:00', '2024-04-05 17:00:00'),
(2112, 0, '2024-05-01 08:00:00', '2024-05-01 17:00:00'),
(2217, 0, '2024-05-02 08:05:00', '2024-05-02 17:05:00'),
(2303, 0, '2024-05-03 08:10:00', '2024-05-03 17:15:00'),
(2414, 0, '2024-05-04 08:15:00', '2024-05-04 17:20:00'),
(2510, 0, '2024-05-05 08:00:00', '2024-05-05 17:00:00'),
(2607, 0, '2024-06-01 08:00:00', '2024-06-01 17:00:00'),
(2712, 0, '2024-06-02 08:10:00', '2024-06-02 17:15:00'),
(2816, 0, '2024-06-03 08:15:00', '2024-06-03 17:20:00'),
(2915, 0, '2024-06-04 08:05:00', '2024-06-04 17:10:00'),
(3011, 0, '2024-06-05 08:00:00', '2024-06-05 17:00:00'),
(3119, 0, '2024-07-01 08:00:00', '2024-07-01 17:00:00'),
(3224, 0, '2024-07-02 08:05:00', '2024-07-02 17:10:00'),
(3318, 0, '2024-07-03 08:10:00', '2024-07-03 17:15:00'),
(3417, 0, '2024-07-04 08:15:00', '2024-07-04 17:20:00'),
(3513, 0, '2024-07-05 08:00:00', '2024-07-05 17:00:00');

-- 勤怠承認テーブルにデータを挿入
INSERT INTO attendance_requests (request_id, employee_id, year_month, status) VALUES
(1, 0, '2024-07', '未')
