package com.levels.ShiftSync.attendacne.service.impl.record;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.attendance.record.impl.ApprovalServiceImpl;

class ApprovalServiceImplTest {

    @Mock
    private AttendanceRecordMapper attendanceRecordMapper;

    @InjectMocks
    private ApprovalServiceImpl approvalServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("承認申請を正常に登録できることを確認するテスト")
    void testInsertApproveRequest() {
        Integer employeeId = 1;
        String yearMonth = "2024-09";

        // 実行
        approvalServiceImpl.insertApproveRequest(employeeId, yearMonth);

        // リポジトリメソッドが正しく呼び出されたか確認
        verify(attendanceRecordMapper, times(1)).insertApproveRequest(employeeId, yearMonth);
    }

    @Test
    @DisplayName("指定した年月の承認申請がない場合はtrueを返すテスト")
    void testIsNoRequest_ReturnsTrue() {
        Integer employeeId = 1;
        String yearMonth = "2024-09";

        // 承認申請がないときはtrueを返す
        when(attendanceRecordMapper.isNoRequest(employeeId, yearMonth)).thenReturn(true);

        // 実行と検証
        boolean result = approvalServiceImpl.isNoRequest(employeeId, yearMonth);
        assertTrue(result);
        verify(attendanceRecordMapper, times(1)).isNoRequest(employeeId, yearMonth);
    }

    @Test
    @DisplayName("指定した年月に承認申請がある場合はfalseを返すテスト")
    void testIsNoRequest_ReturnsFalse() {
        Integer employeeId = 1;
        String yearMonth = "2024-09";

        // 承認申請があるときはfalseを返す
        when(attendanceRecordMapper.isNoRequest(employeeId, yearMonth)).thenReturn(false);

        // 実行と検証
        boolean result = approvalServiceImpl.isNoRequest(employeeId, yearMonth);
        assertFalse(result);
        verify(attendanceRecordMapper, times(1)).isNoRequest(employeeId, yearMonth);
    }

    @Test
    @DisplayName("指定した年月に承認申請がある場合はtrueを返すテスト")
    void testHasRequestsForMonth_ReturnsTrue() {
        Integer employeeId = 1;
        String yearMonth = "2024-09";

        // 承認申請リストが空でない場合
        List<AttendanceRequest> requests = List.of(new AttendanceRequest());
        when(attendanceRecordMapper.getRequestsForMonth(employeeId, yearMonth)).thenReturn(requests);

        // 実行と検証
        boolean result = approvalServiceImpl.hasRequestsForMonth(employeeId, yearMonth);
        assertTrue(result);
        verify(attendanceRecordMapper, times(1)).getRequestsForMonth(employeeId, yearMonth);
    }

    @Test
    @DisplayName("指定した年月に承認申請がない場合はfalseを返すテスト")
    void testHasRequestsForMonth_ReturnsFalse() {
        Integer employeeId = 1;
        String yearMonth = "2024-09";

        // 承認申請リストが空の場合
        when(attendanceRecordMapper.getRequestsForMonth(employeeId, yearMonth)).thenReturn(Collections.emptyList());

        // 実行と検証
        boolean result = approvalServiceImpl.hasRequestsForMonth(employeeId, yearMonth);
        assertFalse(result);
        verify(attendanceRecordMapper, times(1)).getRequestsForMonth(employeeId, yearMonth);
    }
}
