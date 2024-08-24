package com.levels.ShiftSync.service;

import java.sql.Timestamp;
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
     * 出勤時刻を更新するメソッド
     * 
     * @param recordId 出退勤レコードのID。どのレコードを更新するかを特定するために使用します。
     * @param employeeId 従業員のID。どの従業員の出勤時刻を更新するかを特定するために使用します。
     * @param newClockIn 新しい出勤時刻。タイムスタンプ形式（yyyy-MM-dd HH:mm:ss）で指定します。
     */
    void updateClockInTime(Integer recordId, Integer employeeId, Timestamp newClockIn);
    
    /**
     * 従業員の退勤時間を登録するメソッド
     * 現在の時刻を退勤時間としてデータベースに保存します。
     */
    void clockOutTime();
    
    /**
     * 退勤時刻を更新するメソッド
     * 
     * @param recordId 出退勤レコードのID。どのレコードを更新するかを特定するために使用します。
     * @param employeeId 従業員のID。どの従業員の退勤時刻を更新するかを特定するために使用します。
     * @param newClockOut 新しい退勤時刻。タイムスタンプ形式（yyyy-MM-dd HH:mm:ss）で指定します。
     */
    void updateClockOutTime(Integer recordId, Integer employeeId, Timestamp newClockOut);

    /**
     * 従業員の当日の出退勤時間を取得するメソッド
     * @return 当日の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getTodayAttendance();

    /**
     * 任意の月の勤怠記録を取得するメソッド
     * @param month 取得したい月 (1月 = 1, 12月 = 12)
     * @return 指定された月の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getYearlyAttendanceForMonth(int month);
}
