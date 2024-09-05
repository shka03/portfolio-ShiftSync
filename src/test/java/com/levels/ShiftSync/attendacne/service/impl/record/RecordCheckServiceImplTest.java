package com.levels.ShiftSync.attendacne.service.impl.record;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.attendance.record.RecordCheckService;
import com.levels.ShiftSync.service.attendance.record.impl.RecordCheckServiceImpl;

@SpringJUnitConfig
@WebMvcTest(RecordCheckServiceImpl.class)
public class RecordCheckServiceImplTest {

    @MockBean
    private AttendanceRecordMapper attendanceRecordMapper;

    @Autowired
    private RecordCheckService recordCheckService;

    @Test
    @DisplayName("指定された年月に出退勤記録が存在する場合、trueを返すことを確認するテスト")
    void testHasRecordsForMonth_WithRecords() {
        // テスト用の出退勤記録を準備
        List<AttendanceRecord> records = Collections.singletonList(new AttendanceRecord());
        
        // attendanceRecordMapper.getRecordForYearByMonth が出退勤記録を返すように設定
        when(attendanceRecordMapper.getRecordForYearByMonth(anyInt(), anyString())).thenReturn(records);

        // メソッドを呼び出して結果を確認
        boolean result = recordCheckService.hasRecordsForMonth(1, "2024-09");

        // 出退勤記録が存在するため、結果が true であることを検証
        assertTrue(result, "指定された年月に出退勤記録が存在する場合、true を返すべきです");

        // モックが正しく呼び出されたことを確認
        verify(attendanceRecordMapper).getRecordForYearByMonth(1, "2024-09");
    }

    @Test
    @DisplayName("指定された年月に出退勤記録が存在しない場合、falseを返すことを確認するテスト")
    void testHasRecordsForMonth_WithoutRecords() {
        // 出退勤記録が存在しない場合
        List<AttendanceRecord> records = Collections.emptyList();

        // attendanceRecordMapper.getRecordForYearByMonth が空のリストを返すように設定
        when(attendanceRecordMapper.getRecordForYearByMonth(anyInt(), anyString())).thenReturn(records);

        // メソッドを呼び出して結果を確認
        boolean result = recordCheckService.hasRecordsForMonth(1, "2024-09");

        // 出退勤記録が存在しないため、結果が false であることを検証
        assertFalse(result, "指定された年月に出退勤記録が存在しない場合、false を返すべきです");

        // モックが正しく呼び出されたことを確認
        verify(attendanceRecordMapper).getRecordForYearByMonth(1, "2024-09");
    }
}

