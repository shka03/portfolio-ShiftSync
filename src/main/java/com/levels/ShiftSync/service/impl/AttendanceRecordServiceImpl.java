package com.levels.ShiftSync.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.LoginUser;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.AttendanceRecordService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceRecordServiceImpl implements AttendanceRecordService {
	private final AttendanceRecordMapper attendanceRecordMapper;
	
	// TODO：調査->出退勤データを一つの関数にして作成すると出勤のみ時間が生成されない。
    // 出勤時間をデータベースに挿入
    @Override
    public void clockInTime() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(getEmployeeIdFromSecurityContext());
        record.setClockIn(LocalDateTime.now());
        attendanceRecordMapper.clockIn(record);
    }
    
    // 退勤時間をデータベースに挿入
    @Override
    public void clockOutTime() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(getEmployeeIdFromSecurityContext());
        record.setClockOut(LocalDateTime.now());
        attendanceRecordMapper.clockOut(record);
    }
    
    // 認証情報からemployeeIdを取得するメソッド
    private Integer getEmployeeIdFromSecurityContext() {
        // SecurityContextから認証情報を取得
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // LoginUserから従業員IDを取得
        return loginUser.getEmployeeId();
    }

//    // 出勤可能かどうかのチェック
//    public boolean canClockInToday(Integer employeeId) {
//        Optional<AttendanceRecord> record = getTodayRecord(employeeId);
//        return record.isEmpty(); // 今日の記録がない場合に出勤可能
//    }
}
