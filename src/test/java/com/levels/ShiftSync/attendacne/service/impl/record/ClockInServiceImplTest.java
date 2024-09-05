package com.levels.ShiftSync.attendacne.service.impl.record;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.attendance.record.impl.ClockInServiceImpl;
import com.levels.ShiftSync.utility.SecurityUtils;

class ClockInServiceImplTest {

    @Mock
    private AttendanceRecordMapper attendanceRecordMapper;

    @InjectMocks
    private ClockInServiceImpl clockInServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("現在時刻で正常に退勤時間を記録できることを確認するテスト")
    void testClockInTime() {
        // SecurityUtils.getEmployeeIdFromSecurityContext() のモックを作成
        Integer employeeId = 1;

        // 静的メソッドのモックを使用
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getEmployeeIdFromSecurityContext).thenReturn(employeeId);

            // 実行
            clockInServiceImpl.clockInTime();

            // AttendanceRecord に正しい値が設定され、attendanceRecordMapper が呼ばれたことを確認
            verify(attendanceRecordMapper, times(1)).clockIn(any(AttendanceRecord.class));
        }
    }

    @Test
    @DisplayName("出勤時間を正常に更新し、出退勤時間を再計算するテスト")
    void testUpdateClockInTime() {
        Integer recordId = 1;
        Integer employeeId = 1;
        Timestamp newClockIn = Timestamp.valueOf("2024-09-01 09:00:00");

        // getCurrentRecord のモック作成
        AttendanceRecord existingRecord = new AttendanceRecord();
        existingRecord.setClockOut(Timestamp.valueOf("2024-09-01 18:00:00"));
        when(attendanceRecordMapper.getCurrentRecord(recordId)).thenReturn(existingRecord);

        // 実行
        clockInServiceImpl.updateClockInTime(recordId, employeeId, newClockIn);

        // 更新メソッドと勤怠時間再計算メソッドの呼び出しを確認
        verify(attendanceRecordMapper, times(1)).updateClockInTime(anyMap());
        verify(attendanceRecordMapper, times(1)).upsertWorkDuration(recordId, existingRecord.getClockOut(), newClockIn);
    }

    @Test
    @DisplayName("指定したレコードIDで現在の出退勤レコードを取得するテスト")
    void testGetCurrentRecord() throws Exception {
        Integer recordId = 1;
        AttendanceRecord expectedRecord = new AttendanceRecord();
        expectedRecord.setEmployeeId(1);

        // リフレクションを使用して private メソッドにアクセス
        Method method = ClockInServiceImpl.class.getDeclaredMethod("getCurrentRecord", Integer.class);
        method.setAccessible(true);

        // getCurrentRecord のモック作成
        when(attendanceRecordMapper.getCurrentRecord(recordId)).thenReturn(expectedRecord);

        // 実行と検証
        AttendanceRecord actualRecord = (AttendanceRecord) method.invoke(clockInServiceImpl, recordId);
        assertEquals(expectedRecord, actualRecord);
        verify(attendanceRecordMapper, times(1)).getCurrentRecord(recordId);
    }
}
