package com.levels.ShiftSync.repository.attendance.record;

import org.apache.ibatis.annotations.Mapper;

import com.levels.ShiftSync.entity.AttendanceRecord;

@Mapper
public interface ClockInMapper {
    /**
     * @param record 出勤記録を含むAttendanceRecordオブジェクト。出勤時間が設定されている必要があります。
     */
	void insert(AttendanceRecord record);
	
    /**
     * @param recordId 勤怠データのレコードID
     * @return 指定されたレコードを返します。
     */
    AttendanceRecord getCurrentRecord(Integer recordId);
}
