package com.levels.ShiftSync.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.levels.ShiftSync.entity.AttendanceRecord;

@Mapper
public interface AttendanceRecordMapper {
	// 従業員の出勤時間を登録する
    void clockIn(AttendanceRecord record);
    
    // 従業員の退勤時間を登録する
    void clockOut(AttendanceRecord record);
    
    // 従業員の当日の出退勤時間を取得する。
    List<AttendanceRecord> getTodayAttendance(Integer employeeId);
    
    // 従業員の当月の出退勤時間を取得する
    List<AttendanceRecord> getMonthlyAttendance(Integer employeeId);
}
