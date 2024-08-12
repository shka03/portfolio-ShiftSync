package com.levels.ShiftSync.repository;

import org.apache.ibatis.annotations.Mapper;

import com.levels.ShiftSync.entity.AttendanceRecord;

@Mapper
public interface AttendanceRecordMapper {
	// 従業員の出勤時間を登録する
    void clockIn(AttendanceRecord record);
    
    // 従業員の退勤時間を登録する
    void clockOut(AttendanceRecord record);
}
