package com.levels.ShiftSync.attendacne.service.impl.record;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.attendance.record.ClockOutMapper;
import com.levels.ShiftSync.repository.attendance.record.RecordMapper;
import com.levels.ShiftSync.service.attendance.record.impl.ClockOutServiceImpl;
import com.levels.ShiftSync.utility.SecurityUtils;

class ClockOutServiceImplTest {

    @Mock
    private ClockOutMapper clockOutMapper;
    
    @Mock
    private RecordMapper attendanceRecordMapper;

    @InjectMocks
    private ClockOutServiceImpl clockOutServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("正常に退勤時間を記録できることを確認するテスト")
    void testClockOutTime() {
        // SecurityUtils.getEmployeeIdFromSecurityContext() のモックを作成
        Integer employeeId = 1;
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getEmployeeIdFromSecurityContext).thenReturn(employeeId);

            // 実行
            clockOutServiceImpl.insert();

            // 現在時刻を取得し、AttendanceRecord を作成
            Timestamp now = new Timestamp(System.currentTimeMillis());
            verify(clockOutMapper, times(1)).insert(argThat(record -> 
                record.getEmployeeId().equals(employeeId) && 
                Math.abs(record.getClockOut().getTime() - now.getTime()) < 1000 // within 1 second
            ));
        }
    }

    @Test
    @DisplayName("退勤時間を正常に更新し、出退勤時間を再計算するテスト")
    void testUpdateClockOutTime() throws Exception {
        Integer recordId = 1;
        Integer employeeId = 1;
        Timestamp newClockOut = Timestamp.valueOf("2024-09-01 18:00:00");

        // getCurrentRecord のモック作成
        AttendanceRecord existingRecord = new AttendanceRecord();
        existingRecord.setClockIn(Timestamp.valueOf("2024-09-01 09:00:00"));
        when(clockOutMapper.getCurrentRecord(recordId)).thenReturn(existingRecord);

        // 実行
        clockOutServiceImpl.update(recordId, employeeId, newClockOut);

        // 検証
        Map<String, Object> params = new HashMap<>();
        params.put("recordId", recordId);
        params.put("employeeId", employeeId);
        params.put("newClockOut", newClockOut);

        verify(clockOutMapper, times(1)).update(params);
        verify(attendanceRecordMapper, times(1)).upsertWorkDuration(recordId, newClockOut, existingRecord.getClockIn());
    }

    @Test
    @DisplayName("出退勤レコードを正常に取得するテスト")
    void testGetCurrentRecord() throws Exception {
        Integer recordId = 1;
        AttendanceRecord expectedRecord = new AttendanceRecord();
        expectedRecord.setEmployeeId(1);

        // リフレクションを使用して private メソッドにアクセス
        Method method = ClockOutServiceImpl.class.getDeclaredMethod("getCurrentRecord", Integer.class);
        method.setAccessible(true);

        // getCurrentRecord のモック作成
        when(clockOutMapper.getCurrentRecord(recordId)).thenReturn(expectedRecord);

        // 実行と検証
        AttendanceRecord actualRecord = (AttendanceRecord) method.invoke(clockOutServiceImpl, recordId);
        assertEquals(expectedRecord, actualRecord);
        verify(clockOutMapper, times(1)).getCurrentRecord(recordId);
    }
}
