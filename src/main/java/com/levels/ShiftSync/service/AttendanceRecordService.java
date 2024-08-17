package com.levels.ShiftSync.service;

import java.util.List;

import com.levels.ShiftSync.entity.AttendanceRecord;

/**
 * 従業員の出退勤情報を管理するサービスインターフェース
 * このインターフェースは、出勤および退勤の時間の登録、および特定の期間の勤怠記録の取得に関するメソッドを定義しています。
 */
public interface AttendanceRecordService {

    /**
     * 従業員の出勤時間を登録するメソッド
     * 現在の時刻を出勤時間としてデータベースに保存します。
     */
    void clockInTime();

    /**
     * 従業員の退勤時間を登録するメソッド
     * 現在の時刻を退勤時間としてデータベースに保存します。
     */
    void clockOutTime();

    /**
     * 従業員の当日の出退勤時間を取得するメソッド
     * @return 当日の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getTodayAttendance();

    /**
     * 従業員の当月の出退勤時間を取得するメソッド
     * @return 当月の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getMonthlyAttendance();
}
