package com.levels.ShiftSync.repository.attendance.record;

import java.sql.Timestamp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.levels.ShiftSync.entity.AttendanceRecord;

@Mapper
public interface EditClockTimeMapper {

    /**
     * 出勤と退勤の時刻を同時に更新するメソッド。
     * @param recordId 勤怠レコードID
     * @param employeeId 従業員ID
     * @param newClockIn 新しい出勤時刻
     * @param newClockOut 新しい退勤時刻
     */
    void updateClockInAndOut(@Param("recordId") Integer recordId,
                             @Param("employeeId") Integer employeeId,
                             @Param("newClockIn") Timestamp newClockIn,
                             @Param("newClockOut") Timestamp newClockOut);
    
    /**
     * 勤怠修正申請データを登録または更新するメソッド。
     * @param recordId 勤怠レコードID
     * @param employeeId 従業員ID
     * @param yearMonth 年月
     * @param newClockIn 新しい出勤時刻
     * @param newClockOut 新しい退勤時刻
     * @param workDuration 勤務時間（Duration型）
     * @param applicationReason 申請理由
     * @param approvalStatus 承認ステータス
     */
    void upsertClockInAndOut(@Param("recordId") Integer recordId,
                              @Param("employeeId") Integer employeeId,
                              @Param("yearMonthDay") String yearMonthDay,
                              @Param("newClockIn") Timestamp newClockIn,
                              @Param("newClockOut") Timestamp newClockOut,
                              @Param("workDuration") String workDuration,
                              @Param("applicationReason") String applicationReason,
                              @Param("approvalStatus") String approvalStatus);
  
    /**
     * 勤怠レコードの取得メソッド。
     * @param recordId 勤怠レコードID
     * @return 勤怠データ
     */
    AttendanceRecord getCurrentRecord(@Param("recordId") Integer recordId);
}
