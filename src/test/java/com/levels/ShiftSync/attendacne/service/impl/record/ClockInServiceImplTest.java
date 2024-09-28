package com.levels.ShiftSync.attendacne.service.impl.record;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.attendance.record.ClockInMapper;
import com.levels.ShiftSync.repository.attendance.record.WorkDurationMapper;
import com.levels.ShiftSync.service.attendance.record.impl.ClockInServiceImpl;
import com.levels.ShiftSync.utility.SecurityUtils;

class ClockInServiceImplTest {

    @Mock
    private ClockInMapper clockInMapper;
    
	@Mock
	private WorkDurationMapper workDurationMapper;

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
            clockInServiceImpl.insert();

            // AttendanceRecord に正しい値が設定され、attendanceRecordMapper が呼ばれたことを確認
            verify(clockInMapper, times(1)).insert(any(AttendanceRecord.class));
        }
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
        when(clockInMapper.getCurrentRecord(recordId)).thenReturn(expectedRecord);

        // 実行と検証
        AttendanceRecord actualRecord = (AttendanceRecord) method.invoke(clockInServiceImpl, recordId);
        assertEquals(expectedRecord, actualRecord);
        verify(clockInMapper, times(1)).getCurrentRecord(recordId);
    }
}
