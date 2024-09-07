package com.levels.ShiftSync.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.AttendanceRequest;

/**
 * 従業員の勤怠記録を操作するためのMyBatisマッパーインターフェース
 * このインターフェースは、出勤時間と退勤時間の登録、および特定の期間の勤怠記録の取得に関するメソッドを定義しています。
 */
@Mapper
public interface AttendanceRecordMapper {

    /**
     * @param record 出勤記録を含むAttendanceRecordオブジェクト。出勤時間が設定されている必要があります。
     */
    void clockIn(AttendanceRecord record);
    
    /**
     * @param params 更新対象のレコードID、従業員ID、および新しい出勤時間を含むマップ
     */
    void updateClockInTime(Map<String, Object> params);

    /**
     * @param record 退勤記録を含むAttendanceRecordオブジェクト。退勤時間が設定されている必要があります。
     */
    void clockOut(AttendanceRecord record);
    
    /**
     * @param params 更新対象のレコードID、従業員ID、および新しい退勤時間を含むマップ
     */
    void updateClockOutTime(Map<String, Object> params);

    /**
     * @param recordId 出退勤レコードID
     * @param clockIn 出勤時刻
     * @param clockOut 退勤時刻
     */
    void upsertWorkDuration(Integer recordId, Timestamp clockOut, Timestamp clockIn);
    
    /** 
     * @param employeeId 従業員ID。どの従業に関する勤怠履歴を更新するかを特定するために使用します。
     * @param yearMonth 承認対象の年月。年月（yyyy-MM）形式で指定します。
     */
    void insertApproveRequest(Integer employeeId, String yearMonth);
    
    /**
     * @param employeeId 従業員のID
     * @param yearMonth 承認対象の年月。年月（yyyy-MM）形式で指定します。
     * @return 
     */    
    boolean isNoRequest(Integer employeeId, String yearMonth);
    
    /**
     * @param employeeId 従業員ID
     * @param yearMonth 年月 (YYYY-MM)
     * @return 指定された月の承認申請リスト。申請がない場合は空のリストを返します。
     */
    List<AttendanceRequest> getRequestsForMonth(Integer employeeId, String yearMonth);
    
    /**
     * @param employeeId 従業員のID
     * @return 当日の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getTodayRecordForEmployee(Integer employeeId);
    
    /**
     * @param employeeId 従業員のID
     * @param yearMonth 取得したい月 (例: "2023-08")
     * @return 指定された月の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getRecordForYearByMonth(Integer employeeId, String yearMonth);

    /**
     * @param recordId 勤怠データのレコードID
     * @return 指定されたレコードを返します。
     */
    AttendanceRecord getCurrentRecord(Integer recordId);
}
