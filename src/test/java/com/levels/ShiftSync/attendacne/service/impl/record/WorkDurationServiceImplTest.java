package com.levels.ShiftSync.attendacne.service.impl.record;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.List;

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
import com.levels.ShiftSync.repository.attendance.record.WorkDurationMapper;
import com.levels.ShiftSync.service.attendance.record.impl.WorkDurationServiceImpl;

public class WorkDurationServiceImplTest {

    @Mock
    private WorkDurationMapper workDurationMapper;

    @InjectMocks
    private WorkDurationServiceImpl workDurationServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // セキュリティコンテキストをモック
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        
        // LoginUser のモック作成
        LoginUser loginUser = mock(LoginUser.class);
        when(loginUser.getEmployeeId()).thenReturn(1);
        when(authentication.getPrincipal()).thenReturn(loginUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("当日の出退勤レコードに基づいて勤務時間を登録または更新すること")
    void testUpsertTodayWorkDuration() {
        // テスト用のデータ準備
        Timestamp clockIn = Timestamp.valueOf("2024-09-06 09:00:00");
        Timestamp clockOut = Timestamp.valueOf("2024-09-06 17:00:00");
        AttendanceRecord record = new AttendanceRecord();
        record.setRecordId(1);
        record.setClockIn(clockIn);
        record.setClockOut(clockOut);

        // モックの設定
        when(workDurationServiceImpl.getTodayRecordForEmployee()).thenReturn(List.of(record));

        // メソッド呼び出し
        workDurationServiceImpl.upsertTodayWorkDuration();

        // モックが正しく呼び出されたことを確認
        verify(workDurationMapper).upsertWorkDuration(1, clockOut, clockIn);
    }

    @Test
    @DisplayName("出勤・退勤時刻を使って勤務時間を更新すること")
    void testUpsertWorkDuration() {
        // テスト用のデータ準備
        Timestamp newClockIn = Timestamp.valueOf("2024-09-06 08:00:00");
        Timestamp newClockOut = Timestamp.valueOf("2024-09-06 18:00:00");

        // メソッド呼び出し
        workDurationServiceImpl.upsertWorkDuration(1, newClockOut, newClockIn);

        // モックが正しく呼び出されたことを確認
        verify(workDurationMapper).upsertWorkDuration(1, newClockOut, newClockIn);
    }

    @Test
    @DisplayName("指定した月の出退勤記録を取得すること")
    void testGetRecordForYearByMonth() {
        // テスト用のデータ準備
        Timestamp clockIn = Timestamp.valueOf("2024-09-01 09:00:00");
        Timestamp clockOut = Timestamp.valueOf("2024-09-01 17:00:00");
        AttendanceRecord record = new AttendanceRecord();
        record.setClockIn(clockIn);
        record.setClockOut(clockOut);

        // モックの設定
        when(workDurationMapper.getRecordForYearByMonth(anyInt(), anyString()))
                .thenReturn(List.of(record));

        // メソッド呼び出し
        List<AttendanceRecord> records = workDurationServiceImpl.getRecordForYearByMonth(9);

        // 結果の検証
        assertEquals(1, records.size());
        assertEquals(clockIn, records.get(0).getClockIn());
        assertEquals(clockOut, records.get(0).getClockOut());

        // モックが正しく呼び出されたことを確認
        verify(workDurationMapper).getRecordForYearByMonth(1, "2024-09");
    }

    @Test
    @DisplayName("現在ログインしている従業員の当日の出退勤レコードを取得すること")
    void testGetTodayRecordForEmployee() {
        // テスト用のデータ準備
        Timestamp clockIn = Timestamp.valueOf("2024-09-06 09:00:00");
        Timestamp clockOut = Timestamp.valueOf("2024-09-06 17:00:00");
        AttendanceRecord record = new AttendanceRecord();
        record.setClockIn(clockIn);
        record.setClockOut(clockOut);

        // モックの設定
        when(workDurationMapper.getTodayRecordForEmployee(anyInt())).thenReturn(List.of(record));

        // メソッド呼び出し
        List<AttendanceRecord> records = workDurationServiceImpl.getTodayRecordForEmployee();

        // 結果の検証
        assertEquals(1, records.size());
        assertEquals(clockIn, records.get(0).getClockIn());
        assertEquals(clockOut, records.get(0).getClockOut());

        // モックが正しく呼び出されたことを確認
        verify(workDurationMapper).getTodayRecordForEmployee(1);
    }
}
