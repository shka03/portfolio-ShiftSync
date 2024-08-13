package com.levels.ShiftSync.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.levels.ShiftSync.entity.AttendanceRecord;

@Mapper
public interface AttendanceRecordMapper {
	// 従業員の出勤時間を登録する
    void clockIn(AttendanceRecord record);
    
    // 従業員の退勤時間を登録する
    void clockOut(AttendanceRecord record);
    
    // 今日の出勤記録を取得するメソッドを定義
    Optional<AttendanceRecord> findAttendanceRecordForToday(@Param("employeeId") Integer employeeId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
