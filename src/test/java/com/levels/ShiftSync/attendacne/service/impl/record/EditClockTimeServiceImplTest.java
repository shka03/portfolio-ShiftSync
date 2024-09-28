package com.levels.ShiftSync.attendacne.service.impl.record;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.levels.ShiftSync.entity.AttendanceEditRequest;
import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.attendance.record.EditClockTimeMapper;
import com.levels.ShiftSync.service.attendance.record.impl.EditClockTimeServiceImpl;

public class EditClockTimeServiceImplTest {

    @InjectMocks
    private EditClockTimeServiceImpl editClockTimeService;

    @Mock
    private EditClockTimeMapper editClockTimeMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("出勤時刻を正常に修正する")
    void testEditRequestClockInAndOut() {
        // Arrange
        Integer recordId = 1;
        Integer employeeId = 123;
        String yearMonthDay = "2024-09-28";
        Timestamp newClockIn = Timestamp.valueOf("2024-09-28 08:00:00");
        Timestamp newClockOut = Timestamp.valueOf("2024-09-28 17:00:00");
        String applicationReason = "勤務時間の変更";

        // Act
        editClockTimeService.editRequestClockInAndOut(recordId, employeeId, yearMonthDay, newClockIn, newClockOut, applicationReason);

        // Assert
        String expectedWorkDuration = "09:00:00"; // 8時間の勤務時間
        verify(editClockTimeMapper, times(1)).editRequestClockInAndOut(recordId, employeeId, yearMonthDay, newClockIn, newClockOut, expectedWorkDuration, applicationReason);
    }

    @Test
    @DisplayName("現在の出勤記録を正常に取得する")
    void testGetCurrentRecord() {
        // Arrange
        Integer recordId = 1;
        AttendanceRecord expectedRecord = new AttendanceRecord();
        when(editClockTimeMapper.getCurrentRecord(recordId)).thenReturn(expectedRecord);

        // Act
        AttendanceRecord actualRecord = editClockTimeService.getCurrentRecord(recordId);

        // Assert
        assertEquals(expectedRecord, actualRecord, "出勤記録が一致しません。");
        verify(editClockTimeMapper, times(1)).getCurrentRecord(recordId);
    }

    @Test
    @DisplayName("現在の出勤時刻修正申請を正常に取得する")
    void testGetCurrentEditRecord() {
        // Arrange
        Integer employeeId = 123;
        String yearMonthDay = "2024-09-28";
        AttendanceEditRequest expectedRequest = new AttendanceEditRequest();
        when(editClockTimeMapper.getCurrentEditRecord(employeeId, yearMonthDay)).thenReturn(expectedRequest);

        // Act
        AttendanceEditRequest actualRequest = editClockTimeService.getCurrentEditRecord(employeeId, yearMonthDay);

        // Assert
        assertEquals(expectedRequest, actualRequest, "出勤時刻修正申請が一致しません。");
        verify(editClockTimeMapper, times(1)).getCurrentEditRecord(employeeId, yearMonthDay);
    }

}

