-- 認証テーブルへのダミーデータの追加
-- password：adminpass
INSERT INTO authentications (username, password, authority)
VALUES ('admin', '$2a$10$4ws3NzLezXw7nxFY4MRdOuGEqpKEPRmAwRAMnJGvAUOqdagyLd376', 'ADMIN');
-- password：userpass
INSERT INTO authentications (username, password, authority)
VALUES ('user', '$2a$10$/jar9xXQ6lrnVjLvLGv5BepFkLnGIO49RrGx42p2i.1hQt1BZ/7E2', 'USER');
