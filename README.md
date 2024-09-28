# プロジェクト概要
本プロジェクトはSpringBootを使用したポートフォリオになります。<br>
内容は勤怠管理システムでプロジェクト名を「ShiftSync」としてます。

# ShiftSyncの機能について
下記、機能を実装してます。
- ログインとログアウト機能
    - ログインとログアウトする。<br>
    ![login_logout](https://github.com/user-attachments/assets/164c1c49-845b-40c8-91dc-e74f21ec5162)

- 出退勤機能
    - 出退勤時刻を記録する。<br>
    ![2024-09-28_18h01_43](https://github.com/user-attachments/assets/42ed8de8-786a-455a-9f2e-91ba2eca6f88)

- 勤怠履歴
    - 参照した年の年間の勤怠履歴が参照できる。<br>
    ![2024-09-28_18h05_34](https://github.com/user-attachments/assets/060ea294-78b2-43aa-aa13-32aac0aaa400)

- 勤怠履歴のCSVダウンロード
    - 選択した月の勤怠履歴をCSVダウンロードする。<br>
    ![csv_download](https://github.com/user-attachments/assets/3a5b43ea-ede6-4a59-8c0f-ba1062ad7b9f)

- 勤怠データの修正申請
    - 記録済の勤怠データを日付単位で修正申請できる。<br>
    ![勤怠データ修正申請](https://github.com/user-attachments/assets/ebfaf18b-e434-423b-a4b7-f66c851d5f48)
    - 修正申請された内容を承認ができる。<br>
    ![勤怠データ修正承認](https://github.com/user-attachments/assets/ed236968-c2f9-44f7-b347-aceb1310f0bf)

- 勤怠データの承認申請
    - 記録済の勤怠データの承認申請ができる。<br>
    ![勤怠申請](https://github.com/user-attachments/assets/82d8f02d-eda7-40b8-a7b6-bc1a8c2fd260)
    - 承認申請された内容を承認できる。<br>
    ![勤怠申請承認](https://github.com/user-attachments/assets/a8f8456f-c471-4dba-8371-cd8ac850a947)<br>

- エラーページの表示
    - 403/404/500エラーのページをカスタムした内容で表示する。
    - 例：404エラー<br>
    ![404error](https://github.com/user-attachments/assets/5f5da320-7aaa-4ee8-969b-1058c9022f6f)<br>

# セットアップ
THB

# 開発環境
- IDE：Eclipse2023-12
    - SpringBoot 3.3.2
    - 言語：Java21
    - ビルドツール：Gradle
    - コード管理：Git
- SpringBootプロジェクトツール：Spring Initializr
- RDBMS：Postgres
    - GUIツール：Pgadmin4

# テストツール
- テストケース管理：QualityForward
- テスト技法：GIHOZ
- バグ管理：JIRA
- 単体テストツール：Junit5
- 単体テストカバレッジツール：Jacoco
- 画面キャプチャ：Screenpresso
- スプレッドシート：Google sheets

# その他のツール
- コミュニケーション：Chatrwork/GoogleMeet
- プロジェクト管理：JIRA
- ドキュメント管理：Confluence
