package com.levels.ShiftSync.repository.attendance;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.levels.ShiftSync.entity.AttendanceRecord;

@Mapper
public interface RecordMapperProt {
	
    /**
     * @param record 出勤記録を含むAttendanceRecordオブジェクト。出勤時間が設定されている必要があります。
     */
	void add(AttendanceRecord record);
	
    /**
     * @param params 更新対象のレコードID、従業員ID、および新しい出勤時間を含むマップ
     */
	void update(Map<String, Object> params);

}
