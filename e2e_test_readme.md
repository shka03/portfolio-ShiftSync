# テスト実施前の準備内容の紹介
※不明点は調べたうえで相談してく頂けると助かります。

1. [Eclipse](https://willbrains.jp/)をDLする。※可能であればEclipse2023-12を使用してください。
2. Eclipseの設定ファイルを下記手順に従って変更して下さい。
- 下記、画像の様にEclipseの設定ファイル「eclipse.ini」を開きます。
![2024-08-25_16h40_16](https://github.com/user-attachments/assets/04678334-2579-477f-bdca-6b59ae89b357)
- JavaのVer21に変更してください。※画像の箇所の部分です。すでに21の場合は本作業は不要。
![2024-08-25_16h42_55](https://github.com/user-attachments/assets/88e607d4-f2e6-4c35-be5f-fb47686add6c)
3. 本GithubのTagからシステムテストしたい内容をDLする。
4. Eclipseにテスト対象のプロジェクト（DLしたTag）をGradleを利用して下記手順でインポートする。
- eclipseを開いて、ヘッダーにある「ファイル」→「インポート」を選択し、表示される「インポート」画面にて、「既存のGradleプロジェクト」を選択し、「次へ」ボタンをクリックします。
![2024-08-25_16h28_25](https://github.com/user-attachments/assets/b5eaa071-3d72-4186-a718-e99ce7612290)
- 解凍したプロジェクトフォルダを設定し、「完了」ボタンをクリックする。
![2024-08-25_16h28_25](https://github.com/user-attachments/assets/ad43512f-e234-4118-9f30-3854649afd4b)

# タグについて
- 2024/08/25より前のタグはテストデータを挿入するSQLを組み込んでません。<br>
下記、SQL文を src/main/resources/data.sql に挿入してください。<br>
<br>
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
