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

    /**
     * 出勤時間をデータベースに挿入するメソッド
     * 現在の時刻を出勤時間として設定し、データベースに保存します。
     */
    @Override
    public void clockInTime() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(getEmployeeIdFromSecurityContext());
        record.setClockIn(new Timestamp(System.currentTimeMillis()));
        attendanceRecordMapper.clockIn(record);
    }

    /**
     * 退勤時間をデータベースに挿入するメソッド
     * 現在の時刻を退勤時間として設定し、データベースに保存します。
     */
    @Override
    public void clockOutTime() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(getEmployeeIdFromSecurityContext());
        record.setClockOut(new Timestamp(System.currentTimeMillis()));
        attendanceRecordMapper.clockOut(record);
    }

    /**
     * 従業員の当日の出退勤時間を取得するメソッド
     * @return 当日の出退勤時間のリスト
     */
    @Override
    public List<AttendanceRecord> getTodayAttendance() {
        Integer employeeId = getEmployeeIdFromSecurityContext();
        return attendanceRecordMapper.getTodayAttendance(employeeId);
    }

    /**
     * 従業員の当月の出退勤時間を全て取得するメソッド
     * @return 当月の出退勤時間のリスト
     */
    @Override
    public List<AttendanceRecord> getMonthlyAttendance() {
        Integer employeeId = getEmployeeIdFromSecurityContext();
        return attendanceRecordMapper.getMonthlyAttendance(employeeId);
    }

    /**
     * 認証情報から従業員IDを取得するメソッド
     * @return 現在認証されているユーザーの従業員ID
     */
    private Integer getEmployeeIdFromSecurityContext() {
        // SecurityContextから認証情報を取得
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // LoginUserから従業員IDを取得
        return loginUser.getEmployeeId();
    }
}
