package com.levels.ShiftSync.attendacne.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.LoginUser;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.impl.AttendanceRecordServiceImpl;

class AttendanceRecordServiceImplTest {

    @Mock
    private AttendanceRecordMapper attendanceRecordMapper;

    @InjectMocks
    private AttendanceRecordServiceImpl attendanceRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testClockInTime() {
        // セキュリティコンテキストをモックして従業員IDを設定
        Integer mockEmployeeId = 123;
        SecurityContext mockSecurityContext = SecurityContextHolder.createEmptyContext();
        LoginUser mockLoginUser = new LoginUser(mockEmployeeId, "username", "password", List.of());
        mockSecurityContext.setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(mockLoginUser, null));
        SecurityContextHolder.setContext(mockSecurityContext);

        // 出勤時間挿入処理
        attendanceRecordService.clockInTime();

        // AttendanceRecordMapperのメソッド呼び出しを検証
        verify(attendanceRecordMapper).clockIn(any(AttendanceRecord.class));

        // 現在の時刻を模擬するため、適切な値で期待するレコードを作成
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        AttendanceRecord expectedRecord = new AttendanceRecord();
        expectedRecord.setEmployeeId(mockEmployeeId);
        expectedRecord.setClockIn(currentTimestamp);

        // AttendanceRecordMapperが正しい引数で呼び出されるか検証
        when(attendanceRecordMapper.getTodayAttendance(mockEmployeeId)).thenReturn(List.of(expectedRecord));

        List<AttendanceRecord> todayAttendance = attendanceRecordService.getTodayAttendance();
        assertEquals(1, todayAttendance.size(), "今日の勤怠記録が1件であるべきです");
        assertEquals(mockEmployeeId, todayAttendance.get(0).getEmployeeId(), "従業員IDが正しく設定されているべきです");
    }

    @Test
    void testClockOutTime() {
        // セキュリティコンテキストをモックして従業員IDを設定
        Integer mockEmployeeId = 123;
        SecurityContext mockSecurityContext = SecurityContextHolder.createEmptyContext();
        LoginUser mockLoginUser = new LoginUser(mockEmployeeId, "username", "password", List.of());
        mockSecurityContext.setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(mockLoginUser, null));
        SecurityContextHolder.setContext(mockSecurityContext);

        // 退勤時間挿入処理
        attendanceRecordService.clockOutTime();

        // AttendanceRecordMapperのメソッド呼び出しを検証
        verify(attendanceRecordMapper).clockOut(any(AttendanceRecord.class));

        // 現在の時刻を模擬するため、適切な値で期待するレコードを作成
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        AttendanceRecord expectedRecord = new AttendanceRecord();
        expectedRecord.setEmployeeId(mockEmployeeId);
        expectedRecord.setClockOut(currentTimestamp);

        // AttendanceRecordMapperが正しい引数で呼び出されるか検証
        when(attendanceRecordMapper.getTodayAttendance(mockEmployeeId)).thenReturn(List.of(expectedRecord));

        List<AttendanceRecord> todayAttendance = attendanceRecordService.getTodayAttendance();
        assertEquals(1, todayAttendance.size(), "今日の勤怠記録が1件であるべきです");
        assertEquals(mockEmployeeId, todayAttendance.get(0).getEmployeeId(), "従業員IDが正しく設定されているべきです");
    }
    
    @Test
    void testGetTodayAttendance() {
        // セキュリティコンテキストをモックして従業員IDを設定
        Integer mockEmployeeId = 123;
        SecurityContext mockSecurityContext = SecurityContextHolder.createEmptyContext();
        LoginUser mockLoginUser = new LoginUser(mockEmployeeId, "username", "password", List.of());
        mockSecurityContext.setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(mockLoginUser, null));
        SecurityContextHolder.setContext(mockSecurityContext);

        // モックデータの設定
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(mockEmployeeId);
        record.setClockIn(new Timestamp(System.currentTimeMillis()));

        when(attendanceRecordMapper.getTodayAttendance(mockEmployeeId)).thenReturn(List.of(record));

        List<AttendanceRecord> result = attendanceRecordService.getTodayAttendance();
        assertEquals(1, result.size(), "今日の勤怠記録が1件であるべきです");
        assertEquals(mockEmployeeId, result.get(0).getEmployeeId(), "従業員IDが正しく設定されているべきです");
    }
    
    @Test
    void testGetYearlyAttendanceForMonth() {
        // セキュリティコンテキストをモックして従業員IDを設定
        Integer mockEmployeeId = 123;
        SecurityContext mockSecurityContext = SecurityContextHolder.createEmptyContext();
        LoginUser mockLoginUser = new LoginUser(mockEmployeeId, "username", "password", List.of());
        mockSecurityContext.setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(mockLoginUser, null));
        SecurityContextHolder.setContext(mockSecurityContext);

        // 現在の年と月を模擬するための設定
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int month = 5; // 例として5月を指定
        String yearMonth = String.format("%d-%02d", currentYear, month);

        // モックデータの設定
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(mockEmployeeId);
        record.setClockIn(new Timestamp(System.currentTimeMillis()));

        when(attendanceRecordMapper.getMonthlyAttendanceForYear(mockEmployeeId, yearMonth)).thenReturn(List.of(record));

        // メソッドの実行と検証
        List<AttendanceRecord> result = attendanceRecordService.getYearlyAttendanceForMonth(month);
        assertEquals(1, result.size(), "指定された月の勤怠記録が1件であるべきです");
        assertEquals(mockEmployeeId, result.get(0).getEmployeeId(), "従業員IDが正しく設定されているべきです");
    }
}
