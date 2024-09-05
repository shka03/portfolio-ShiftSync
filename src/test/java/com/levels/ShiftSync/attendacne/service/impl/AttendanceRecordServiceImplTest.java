package com.levels.ShiftSync.attendacne.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.LoginUser;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.attendance.record.impl.ClockInServiceImpl;
import com.levels.ShiftSync.service.attendance.record.impl.ClockOutServiceImpl;
import com.levels.ShiftSync.service.attendance.record.impl.WorkDurationServiceImpl;

public class AttendanceRecordServiceImplTest {

    @Mock
    private AttendanceRecordMapper attendanceRecordMapper;
    
    @InjectMocks
    private ClockInServiceImpl clockInServiceImpl;
    
    @InjectMocks
    private ClockOutServiceImpl clockOutServiceImpl;
    
    @InjectMocks
    private WorkDurationServiceImpl workDurationServiceImpl;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 出勤処理が成功し、出勤記録がデータベースに保存されることを確認するテスト。
     * SecurityContextにユーザー情報を設定し、出勤処理を実行して、出勤時刻が現在時刻であることを検証します。
     */
    @Test
    @DisplayName("出勤処理が成功し、出勤時刻が正しく保存されるテスト")
    void testClockInTime() {
        // SecurityContextのモックを作成
        LoginUser loginUser = new LoginUser(1, "testuser", "password", Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(loginUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 出勤処理を実行
        clockInServiceImpl.clockInTime();

        // 出勤時刻が現在時刻であることを確認
        Timestamp now = new Timestamp(System.currentTimeMillis());
        verify(attendanceRecordMapper, times(1)).clockIn(argThat(record -> 
            record.getEmployeeId().equals(1) &&
            record.getClockIn().toInstant().toEpochMilli() >= now.toInstant().toEpochMilli() - 1000 &&
            record.getClockIn().toInstant().toEpochMilli() <= now.toInstant().toEpochMilli() + 1000
        ));
    }

    /**
     * 出勤時刻の更新処理が正しく行われることを確認するテスト。
     * 現在の出勤記録をモックし、出勤時刻を更新して、勤務時間も正しく更新されるか検証します。
     */
    @Test
    @DisplayName("出勤時刻の更新処理が成功するテスト")
    void testUpdateClockInTime() {
        Integer recordId = 1;
        Integer employeeId = 1;
        Timestamp newClockIn = new Timestamp(System.currentTimeMillis());

        // 現在の出勤記録をモック
        AttendanceRecord currentRecord = new AttendanceRecord();
        currentRecord.setClockOut(new Timestamp(System.currentTimeMillis() + 3600000)); // +1 hour
        when(attendanceRecordMapper.getCurrentRecord(recordId)).thenReturn(currentRecord);

        // 出勤時刻の更新処理を実行
        clockInServiceImpl.updateClockInTime(recordId, employeeId, newClockIn);

        // 出勤時刻と勤務時間の更新が正しく行われることを確認
        Map<String, Object> params = new HashMap<>();
        params.put("recordId", recordId);
        params.put("employeeId", employeeId);
        params.put("newClockIn", newClockIn);

        verify(attendanceRecordMapper, times(1)).updateClockInTime(params);
        verify(attendanceRecordMapper, times(1)).upsertWorkDuration(recordId, currentRecord.getClockOut(), newClockIn);
    }

    /**
     * 退勤処理が成功し、退勤記録がデータベースに保存されることを確認するテスト。
     * SecurityContextにユーザー情報を設定し、退勤処理を実行して、退勤時刻が現在時刻であることを検証します。
     */
    @Test
    @DisplayName("退勤処理が成功し、退勤時刻が正しく保存されるテスト")
    void testClockOutTime() {
        // SecurityContextのモックを作成
        LoginUser loginUser = new LoginUser(1, "testuser", "password", Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(loginUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 退勤処理を実行
        clockOutServiceImpl.clockOutTime();

        // 退勤時刻が現在時刻であることを確認
        Timestamp now = new Timestamp(System.currentTimeMillis());
        verify(attendanceRecordMapper, times(1)).clockOut(argThat(record -> 
            record.getEmployeeId().equals(1) &&
            record.getClockOut().toInstant().toEpochMilli() >= now.toInstant().toEpochMilli() - 1000 &&
            record.getClockOut().toInstant().toEpochMilli() <= now.toInstant().toEpochMilli() + 1000
        ));
    }

    /**
     * 退勤時刻の更新処理が正しく行われることを確認するテスト。
     * 現在の退勤記録をモックし、退勤時刻を更新して、勤務時間も正しく更新されるか検証します。
     */
    @Test
    @DisplayName("退勤時刻の更新処理が成功するテスト")
    void testUpdateClockOutTime() {
        Integer recordId = 1;
        Integer employeeId = 1;
        Timestamp newClockOut = new Timestamp(System.currentTimeMillis());

        // 現在の退勤記録をモック
        AttendanceRecord currentRecord = new AttendanceRecord();
        currentRecord.setClockIn(new Timestamp(System.currentTimeMillis() - 3600000)); // -1 hour
        when(attendanceRecordMapper.getCurrentRecord(recordId)).thenReturn(currentRecord);

        // 退勤時刻の更新処理を実行
        clockOutServiceImpl.updateClockOutTime(recordId, employeeId, newClockOut);

        // 退勤時刻と勤務時間の更新が正しく行われることを確認
        Map<String, Object> params = new HashMap<>();
        params.put("recordId", recordId);
        params.put("employeeId", employeeId);
        params.put("newClockOut", newClockOut);

        verify(attendanceRecordMapper, times(1)).updateClockOutTime(params);
        verify(attendanceRecordMapper, times(1)).upsertWorkDuration(recordId, newClockOut, currentRecord.getClockIn());
    }

    /**
     * 当日の勤務時間が正しく登録・更新されることを確認するテスト。
     * 当日の出勤記録をモックし、勤務時間が正しく登録・更新されるか検証します。
     */
    @Test
    @DisplayName("当日の勤務時間の登録・更新が正しく行われるテスト")
    void testUpsertTodayWorkDuration() {
        // 当日の出勤記録をモック
        Timestamp clockIn = new Timestamp(System.currentTimeMillis() - 3600000); // -1 hour
        Timestamp clockOut = new Timestamp(System.currentTimeMillis());
        AttendanceRecord todayRecord = new AttendanceRecord();
        todayRecord.setRecordId(1);
        todayRecord.setClockIn(clockIn);
        todayRecord.setClockOut(clockOut);

        when(attendanceRecordMapper.getTodayRecordForEmployee(anyInt())).thenReturn(Collections.singletonList(todayRecord));

        // 勤務時間の登録・更新処理を実行
        workDurationServiceImpl.upsertTodayWorkDuration();

        // 勤務時間の登録・更新が正しく行われることを確認
        verify(attendanceRecordMapper, times(1)).upsertWorkDuration(
            eq(todayRecord.getRecordId()),
            eq(todayRecord.getClockOut()),
            eq(todayRecord.getClockIn())
        );
    }


    /**
     * 指定された月の出退勤記録が正しく取得されることを確認するテスト。
     * セキュリティコンテキストにユーザー情報を設定し、指定された月の出退勤記録が正しく取得されるか検証します。
     */
    @Test
    @DisplayName("指定された月の出退勤記録が正しく取得されるテスト")
    void testGetYearMonthRecord() {
        int month = 8; // 8月
        
        // SecurityContextのモックを作成
        LoginUser loginUser = new LoginUser(1, "testuser", "password", Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(loginUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // モックの設定
        List<AttendanceRecord> records = Collections.emptyList();
        when(attendanceRecordMapper.getRecordForYearByMonth(anyInt(), anyString())).thenReturn(records);

        // 月別出退勤記録取得メソッドを実行
        List<AttendanceRecord> result = workDurationServiceImpl.getRecordForYearByMonth(month);

        // メソッド呼び出しの確認
        String expectedYearMonth = String.format("%d-%02d", Calendar.getInstance().get(Calendar.YEAR), month);
        verify(attendanceRecordMapper, times(1)).getRecordForYearByMonth(1, expectedYearMonth);

        // 結果の検証
        assertNotNull(result);
        assertEquals(records, result);
    }
}
