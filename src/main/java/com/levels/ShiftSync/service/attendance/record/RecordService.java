package com.levels.ShiftSync.service.attendance.record;

import java.sql.Timestamp;

import com.levels.ShiftSync.entity.AttendanceRecord;

public interface RecordService {
    /**
     * 現在の時刻で出勤時間を記録します。
     */
    void insert();
    
    /**
     * 指定されたレコードの出勤時間を更新し、出退勤時間を再計算します。
     * 
     * @param recordId 出退勤レコードのID
     * @param employeeId 従業員のID
     * @param newClockIn 新しい出勤時刻
     */
    void update(Integer recordId, Integer employeeId, Timestamp newClockIn);
    
    /**
     * @param recordId 勤怠データのレコードID
     * @return 指定されたレコードを返します。
     */
    AttendanceRecord getCurrentRecord(Integer recordId);
}
