package com.levels.ShiftSync.service;

import java.util.List;

import com.levels.ShiftSync.entity.AttendanceRecord;

public interface AttendanceRecordService {
	// 従業員の出勤時間を登録する
    void clockInTime();
	
    // 従業員の退勤時間を登録する
    void clockOutTime();
    
    // 従業員の当月の出退勤時間を取得する
    List<AttendanceRecord>  getMonthlyAttendance();
}
