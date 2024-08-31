-- 既存のテーブルと型を削除する
DROP TABLE IF EXISTS attendance_records CASCADE;
DROP TABLE IF EXISTS authentications CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS attendance_requests CASCADE;
DROP TYPE IF EXISTS role CASCADE;

-- 権限用のENUM型を作成する
CREATE TYPE role AS ENUM ('ADMIN', 'USER');

-- 従業員情報を格納するテーブルを作成する
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

-- 認証情報を格納するテーブルを作成する
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

-- 勤怠記録を格納するテーブルを作成する
CREATE TABLE attendance_records (
    -- レコードID：主キー
    record_id SERIAL PRIMARY KEY,
    -- 従業員ID：外部キー
    employee_id INTEGER REFERENCES employees(employee_id),
    -- 出勤時間
    clock_in timestamp without time zone,
    -- 退勤時間
    clock_out timestamp without time zone,
    -- 勤務時間
    work_duration interval
);

-- 勤怠リクエストを格納するテーブルを作成する
CREATE TABLE attendance_requests (
    -- リクエストID：主キー
    request_id SERIAL PRIMARY KEY,
    -- 従業員ID：外部キー
    employee_id INTEGER REFERENCES employees(employee_id),
    -- 年月
    year_month VARCHAR(7) NOT NULL,
    -- ステータス
    status VARCHAR(3)
);
