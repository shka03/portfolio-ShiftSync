package com.levels.ShiftSync.service;

public interface AttendanceRecordService {
	// 従業員の出勤時間を登録する
    void clockInTime();
	// 従業員の退勤時間を登録する
    void clockOutTime();
}
