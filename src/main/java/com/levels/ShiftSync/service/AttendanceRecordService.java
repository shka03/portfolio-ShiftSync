package com.levels.ShiftSync.service;

import java.sql.Timestamp;
import java.util.List;

import com.levels.ShiftSync.entity.AttendanceRecord;

/**
 * 従業員の出退勤情報を管理するサービスインターフェース
 * このインターフェースは、出勤および退勤の時間の登録、および特定の期間の勤怠記録の取得に関するメソッドを定義しています。
 */
public interface AttendanceRecordService {

    void clockInTime();

    /**
     * @param recordId 出退勤レコードのID。どのレコードを更新するかを特定するために使用します。
     * @param employeeId 従業員のID。どの従業員の出勤時刻を更新するかを特定するために使用します。
     * @param newClockIn 新しい出勤時刻。タイムスタンプ形式（yyyy-MM-dd HH:mm:ss）で指定します。
     */
    void updateClockInTime(Integer recordId, Integer employeeId, Timestamp newClockIn);
    
    void clockOutTime();
    
    /**
     * @param recordId 出退勤レコードのID。どのレコードを更新するかを特定するために使用します。
     * @param employeeId 従業員のID。どの従業員の退勤時刻を更新するかを特定するために使用します。
     * @param newClockOut 新しい退勤時刻。タイムスタンプ形式（yyyy-MM-dd HH:mm:ss）で指定します。
     */
    void updateClockOutTime(Integer recordId, Integer employeeId, Timestamp newClockOut);
    
    void upsertTodayWorkDuration();
    
    /**
     * @param recordId 出退勤レコードのID。どのレコードを更新するかを特定するために使用します。
     * @param newClockIn 新しい出勤時刻。タイムスタンプ形式（yyyy-MM-dd HH:mm:ss）で指定します。
     * @param newClockOut 新しい退勤時刻。タイムスタンプ形式（yyyy-MM-dd HH:mm:ss）で指定します。
     */
    void upsertWorkDuration(Integer recordId, Timestamp newClockOut, Timestamp newClockIn);

    /**
     * @param employeeId 従業員ID。どの従業に関する勤怠履歴を更新するかを特定するために使用します。
     * @param yearMonth 承認対象の年月。年月（yyyy-MM）形式で指定します。
     */
    void insertApproveRequest(Integer employeeId, String yearMonth);
    
    /**
     * @return 当日の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getTodayRecordForEmployee();

    /**
     * @param month 取得したい月
     * @return 指定された月の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getRecordForYearByMonth(int month);
}
