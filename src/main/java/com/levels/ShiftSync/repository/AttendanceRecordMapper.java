package com.levels.ShiftSync.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.levels.ShiftSync.entity.AttendanceRecord;

/**
 * 従業員の勤怠記録を操作するためのMyBatisマッパーインターフェース
 * このインターフェースは、出勤時間と退勤時間の登録、および特定の期間の勤怠記録の取得に関するメソッドを定義しています。
 */
@Mapper
public interface AttendanceRecordMapper {

    /**
     * 従業員の出勤時間をデータベースに登録するメソッド
     * @param record 出勤記録を含むAttendanceRecordオブジェクト。出勤時間が設定されている必要があります。
     */
    void clockIn(AttendanceRecord record);
    
    /**
     * 従業員の出勤時間を修正するメソッド
     * @param params 更新対象のレコードID、従業員ID、および新しい出勤時間を含むマップ
     */
    void updateClockInTime(Map<String, Object> params);

    /**
     * 従業員の退勤時間をデータベースに登録するメソッド
     * @param record 退勤記録を含むAttendanceRecordオブジェクト。退勤時間が設定されている必要があります。
     */
    void clockOut(AttendanceRecord record);
    
    /**
     * 従業員の退勤時間を修正するメソッド
     * @param params 更新対象のレコードID、従業員ID、および新しい退勤時間を含むマップ
     */
    void updateClockOutTime(Map<String, Object> params);

    /**
     * 当日の勤怠時間を登録・更新するメソッド
     * @param recordId 出退勤レコードID
     * @param clockIn 出勤時刻
     * @param clockOut 退勤時刻
     */
    void upsertWorkDuration(Integer recordId, Timestamp clockOut, Timestamp clockIn);
    
    /**
     * 従業員の当日の出退勤時間を取得するメソッド
     * @param employeeId 従業員のID
     * @return 当日の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getTodayAttendance(Integer employeeId);
    
    /**
     * 任意の月の勤怠記録を取得するメソッド
     * @param employeeId 従業員のID
     * @param yearMonth 取得したい月 (例: "2023-08")
     * @return 指定された月の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getMonthlyAttendanceForYear(Integer employeeId, String yearMonth);
    
//    TODO：void getMonthlyTotalWorkHours();月の合計勤務時間 
}
