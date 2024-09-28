package com.levels.ShiftSync.service.attendance.record;

import com.levels.ShiftSync.entity.AttendanceRecord;

public interface RecordService {
    /**
     * 現在の時刻で出勤時間を記録します。
     */
    void insert();
    
    /**
     * @param recordId 勤怠データのレコードID
     * @return 指定されたレコードを返します。
     */
    AttendanceRecord getCurrentRecord(Integer recordId);
}
