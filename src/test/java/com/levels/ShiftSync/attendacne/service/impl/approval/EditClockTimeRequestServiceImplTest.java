package com.levels.ShiftSync.attendacne.service.impl.approval;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.levels.ShiftSync.entity.AttendanceEditRequest;
import com.levels.ShiftSync.repository.attendance.approval.EditClockTimeRequestMapper;
import com.levels.ShiftSync.service.attendance.approval.impl.EditClockTimeRequestServiceImpl;

public class EditClockTimeRequestServiceImplTest {

    @InjectMocks
    private EditClockTimeRequestServiceImpl editClockTimeRequestService;

    @Mock
    private EditClockTimeRequestMapper editClockTimeRequestMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("全ての出勤時刻修正申請を取得する")
    void testGetAllRequests() {
        // Arrange
        AttendanceEditRequest request1 = new AttendanceEditRequest();
        AttendanceEditRequest request2 = new AttendanceEditRequest();
        List<AttendanceEditRequest> expectedRequests = Arrays.asList(request1, request2);
        
        when(editClockTimeRequestMapper.getAllRequests()).thenReturn(expectedRequests);

        // Act
        List<AttendanceEditRequest> actualRequests = editClockTimeRequestService.getAllRequests();

        // Assert
        assertEquals(expectedRequests, actualRequests, "出勤時刻修正申請のリストが一致しません。");
        verify(editClockTimeRequestMapper, times(1)).getAllRequests();
    }

    @Test
    @DisplayName("特定の従業員の出勤時刻修正申請を取得する")
    void testGetCurrentEditRecord() {
        // Arrange
        Integer employeeId = 123;
        String yearMonthDay = "2024-09-28";
        AttendanceEditRequest request = new AttendanceEditRequest();
        List<AttendanceEditRequest> expectedRequests = Arrays.asList(request);

        when(editClockTimeRequestMapper.getCurrentEditRecord(employeeId, yearMonthDay)).thenReturn(expectedRequests);

        // Act
        List<AttendanceEditRequest> actualRequests = editClockTimeRequestService.getCurrentEditRecord(employeeId, yearMonthDay);

        // Assert
        assertEquals(expectedRequests, actualRequests, "出勤時刻修正申請のリストが一致しません。");
        verify(editClockTimeRequestMapper, times(1)).getCurrentEditRecord(employeeId, yearMonthDay);
    }

    @Test
    @DisplayName("出勤時刻修正申請を正常に更新する")
    void testUpdateEditClockTimeRecord() {
        // Arrange
        Integer recordId = 1;
        Integer employeeId = 123;
        Timestamp newClockInTime = Timestamp.valueOf("2024-09-28 08:00:00");
        Timestamp newClockOutTime = Timestamp.valueOf("2024-09-28 17:00:00");
        String newWorkDuration = "9:00:00";

        // Act
        editClockTimeRequestService.updateEditClockTimeRecord(recordId, employeeId, newClockInTime, newClockOutTime, newWorkDuration);

        // Assert
        verify(editClockTimeRequestMapper, times(1)).updateEditClockTimeRecord(recordId, employeeId, newClockInTime, newClockOutTime, newWorkDuration);
    }

    @Test
    @DisplayName("出勤時刻修正申請を正常に削除する")
    void testDeleteEditClockTimeApproval() {
        // Arrange
        Integer employeeId = 123;
        String yearMonthDay = "2024-09-28";

        // Act
        editClockTimeRequestService.deleteEditClockTimeApproval(employeeId, yearMonthDay);

        // Assert
        verify(editClockTimeRequestMapper, times(1)).deleteEditClockTimeApproval(employeeId, yearMonthDay);
    }
}
