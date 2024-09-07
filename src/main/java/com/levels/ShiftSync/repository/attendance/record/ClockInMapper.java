package com.levels.ShiftSync.repository.attendance.record;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.levels.ShiftSync.entity.AttendanceRecord;

@Mapper
public interface ClockInMapper {
    /**
     * @param record 出勤記録を含むAttendanceRecordオブジェクト。出勤時間が設定されている必要があります。
     */
	void insert(AttendanceRecord record);
	
    /**
     * @param params 更新対象のレコードID、従業員ID、および新しい出勤時間を含むマップ
     */
	void update(Map<String, Object> params);
	
    /**
     * @param recordId 勤怠データのレコードID
     * @return 指定されたレコードを返します。
     */
    AttendanceRecord getCurrentRecord(Integer recordId);
}
