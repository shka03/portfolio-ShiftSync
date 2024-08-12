-- テーブルが存在したら削除する（依存オブジェクトも削除する）
DROP TABLE IF EXISTS attendance_records CASCADE;
DROP TABLE IF EXISTS authentications CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TYPE IF EXISTS role CASCADE;

-- 権限用のENUM型
CREATE TYPE role AS ENUM ('ADMIN', 'USER');


-- 従業員情報を格納するテーブル
CREATE TABLE employees (
    -- 従業員ID：主キー
    employee_id INTEGER PRIMARY KEY,
    -- 氏名
    name VARCHAR(100) NOT NULL,
    -- メールアドレス
    email VARCHAR(100) UNIQUE NOT NULL,
    -- 部署
    department VARCHAR(50),
    -- 電話番号
    phone_number VARCHAR(20),
    -- 生年月日
    date_of_birth DATE
);

-- 認証情報を格納するテーブル
CREATE TABLE authentications (
    -- 従業員ID：外部キーとして定義
    employee_id INTEGER PRIMARY KEY REFERENCES employees(employee_id),
    -- ユーザー名
    username VARCHAR(50) NOT NULL,
    -- パスワード
    password VARCHAR(255) NOT NULL,
    -- 権限
    authority role NOT NULL
);

-- 勤怠記録を格納するテーブル
CREATE TABLE attendance_records (
    -- レコードID：主キー
    record_id SERIAL PRIMARY KEY,
    -- 従業員ID：外部キー
    employee_id INTEGER REFERENCES employees(employee_id),
    -- 出勤時間
    clock_in timestamp without time zone,
    -- 退勤時間
    clock_out timestamp without time zone
);
