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

