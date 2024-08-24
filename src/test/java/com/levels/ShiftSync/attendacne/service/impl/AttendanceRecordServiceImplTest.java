package com.levels.ShiftSync.attendacne.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private AttendanceRecordServiceImpl attendanceRecordServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockSecurityContext(Integer employeeId) {
        SecurityContext mockSecurityContext = SecurityContextHolder.createEmptyContext();
        LoginUser mockLoginUser = new LoginUser(employeeId, "username", "password", List.of());
        mockSecurityContext.setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(mockLoginUser, null)
        );
        SecurityContextHolder.setContext(mockSecurityContext);
    }

    @Test
    @DisplayName("出勤時刻を正常に記録するテスト")
    void testClockInTime_Success() {
        // セキュリティコンテキストを設定
        Integer mockEmployeeId = 123;
        mockSecurityContext(mockEmployeeId);

        // モックの設定
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        AttendanceRecord expectedRecord = new AttendanceRecord();
        expectedRecord.setEmployeeId(mockEmployeeId);
        expectedRecord.setClockIn(currentTimestamp);

        // メソッドの実行
        attendanceRecordServiceImpl.clockInTime();

        // メソッドが呼び出されたことを確認
        verify(attendanceRecordMapper).clockIn(any(AttendanceRecord.class));

        // モックデータを返すように設定
        when(attendanceRecordMapper.getTodayAttendance(mockEmployeeId)).thenReturn(List.of(expectedRecord));

        // 結果の検証
        List<AttendanceRecord> todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();
        assertEquals(1, todayAttendance.size(), "今日の勤怠記録が1件であるべきです");
        assertEquals(mockEmployeeId, todayAttendance.get(0).getEmployeeId(), "従業員IDが正しく設定されているべきです");
    }

    @Test
    @DisplayName("退勤時刻を正常に記録するテスト")
    void testClockOutTime_Success() {
        // セキュリティコンテキストを設定
        Integer mockEmployeeId = 123;
        mockSecurityContext(mockEmployeeId);

        // モックの設定
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        AttendanceRecord expectedRecord = new AttendanceRecord();
        expectedRecord.setEmployeeId(mockEmployeeId);
        expectedRecord.setClockOut(currentTimestamp);

        // メソッドの実行
        attendanceRecordServiceImpl.clockOutTime();

        // メソッドが呼び出されたことを確認
        verify(attendanceRecordMapper).clockOut(any(AttendanceRecord.class));

        // モックデータを返すように設定
        when(attendanceRecordMapper.getTodayAttendance(mockEmployeeId)).thenReturn(List.of(expectedRecord));

        // 結果の検証
        List<AttendanceRecord> todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();
        assertEquals(1, todayAttendance.size(), "今日の勤怠記録が1件であるべきです");
        assertEquals(mockEmployeeId, todayAttendance.get(0).getEmployeeId(), "従業員IDが正しく設定されているべきです");
    }

    @Test
    @DisplayName("出勤時刻更新メソッドが正常に動作する場合のテスト")
    void testUpdateClockInTime_Success() {
        // モックの設定
        doNothing().when(attendanceRecordMapper).updateClockInTime(anyMap());

        // テスト実行
        Timestamp newClockIn = Timestamp.valueOf("2024-08-22 08:30:00");
        attendanceRecordServiceImpl.updateClockInTime(1, 1, newClockIn);

        // メソッドが正しいパラメータで呼ばれたことを確認
        Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put("recordId", 1);
        expectedParams.put("employeeId", 1);
        expectedParams.put("newClockIn", newClockIn);

        verify(attendanceRecordMapper).updateClockInTime(expectedParams);
    }

    @Test
    @DisplayName("無効なパラメータで出勤時刻を更新するテスト")
    void testUpdateClockInTime_InvalidParams() {
        // モックの設定
        doNothing().when(attendanceRecordMapper).updateClockInTime(anyMap());

        // テスト実行
        attendanceRecordServiceImpl.updateClockInTime(1, 1, null);

        // メソッドが正しいパラメータで呼ばれたことを確認
        Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put("recordId", 1);
        expectedParams.put("employeeId", 1);
        expectedParams.put("newClockIn", null);

        verify(attendanceRecordMapper).updateClockInTime(expectedParams);
    }

    @Test
    @DisplayName("退勤時刻更新メソッドが正常に動作する場合のテスト")
    void testUpdateClockOutTime_Success() {
        // モックの設定
        doNothing().when(attendanceRecordMapper).updateClockOutTime(anyMap());

        // テスト実行
        Timestamp newClockOut = Timestamp.valueOf("2024-08-22 17:30:00");
        attendanceRecordServiceImpl.updateClockOutTime(1, 1, newClockOut);

        // メソッドが正しいパラメータで呼ばれたことを確認
        Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put("recordId", 1);
        expectedParams.put("employeeId", 1);
        expectedParams.put("newClockOut", newClockOut);

        verify(attendanceRecordMapper).updateClockOutTime(expectedParams);
    }

    @Test
    @DisplayName("無効なパラメータで退勤時刻を更新するテスト")
    void testUpdateClockOutTime_InvalidParams() {
        // モックの設定
        doNothing().when(attendanceRecordMapper).updateClockOutTime(anyMap());

        // テスト実行
        attendanceRecordServiceImpl.updateClockOutTime(1, 1, null);

        // メソッドが正しいパラメータで呼ばれたことを確認
        Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put("recordId", 1);
        expectedParams.put("employeeId", 1);
        expectedParams.put("newClockOut", null);

        verify(attendanceRecordMapper).updateClockOutTime(expectedParams);
    }
    
    @Test
    @DisplayName("今日の勤怠記録を取得するテスト")
    void testGetTodayAttendance() {
        // セキュリティコンテキストを設定
        Integer mockEmployeeId = 123;
        mockSecurityContext(mockEmployeeId);

        // モックデータの設定
        Timestamp clockInTimestamp = new Timestamp(System.currentTimeMillis());
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(mockEmployeeId);
        record.setClockIn(clockInTimestamp);

        when(attendanceRecordMapper.getTodayAttendance(mockEmployeeId)).thenReturn(List.of(record));

        // メソッドの実行と検証
        List<AttendanceRecord> result = attendanceRecordServiceImpl.getTodayAttendance();
        assertEquals(1, result.size(), "今日の勤怠記録が1件であるべきです");
        assertEquals(mockEmployeeId, result.get(0).getEmployeeId(), "従業員IDが正しく設定されているべきです");
    }
    
    @Test
    @DisplayName("指定した月の年間勤怠記録を取得するテスト")
    void testGetYearlyAttendanceForMonth() {
        // セキュリティコンテキストを設定
        Integer mockEmployeeId = 123;
        mockSecurityContext(mockEmployeeId);

        // 現在の年と月を模擬する
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
        List<AttendanceRecord> result = attendanceRecordServiceImpl.getYearlyAttendanceForMonth(month);
        assertEquals(1, result.size(), "指定された月の勤怠記録が1件であるべきです");
        assertEquals(mockEmployeeId, result.get(0).getEmployeeId(), "従業員IDが正しく設定されているべきです");
    }
}
