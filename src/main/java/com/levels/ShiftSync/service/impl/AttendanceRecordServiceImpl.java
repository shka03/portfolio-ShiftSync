package com.levels.ShiftSync.service.impl;

import java.sql.Timestamp;
import java.util.List;

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
	
    // 出勤時間をデータベースに挿入
    @Override
    public void clockInTime() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(getEmployeeIdFromSecurityContext());
        record.setClockIn(new Timestamp(System.currentTimeMillis()));
        attendanceRecordMapper.clockIn(record);
    }
    
    // 退勤時間をデータベースに挿入
    @Override
    public void clockOutTime() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(getEmployeeIdFromSecurityContext());
        record.setClockOut(new Timestamp(System.currentTimeMillis()));
        attendanceRecordMapper.clockOut(record);
    }
    
    // 従業員の当月の出退勤時間を全て取得する
    @Override
    public List<AttendanceRecord> getMonthlyAttendance() {
        Integer employeeId = getEmployeeIdFromSecurityContext();
        List<AttendanceRecord> attendanceRecords = attendanceRecordMapper.getMonthlyAttendance(employeeId);
        return attendanceRecords;
    }
    
    // 認証情報からemployeeIdを取得する
    private Integer getEmployeeIdFromSecurityContext() {
        // SecurityContextから認証情報を取得
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // LoginUserから従業員IDを取得
        return loginUser.getEmployeeId();
    }
}
