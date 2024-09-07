package com.levels.ShiftSync.attendacne.service.impl.approval;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.repository.AttendanceRequestMapper;
import com.levels.ShiftSync.service.attendance.approval.impl.RequestServiceImpl;

public class RequestServiceImplTest {

    @Mock
    private AttendanceRequestMapper attendanceRequestMapper;

    @InjectMocks
    private RequestServiceImpl requestServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("全ての承認申請を正しく取得できること")
    void testGetAllRequests() {
        // テスト用データの準備
        List<AttendanceRequest> requests = List.of(
            new AttendanceRequest(1, 1, "2024-09", "提出中"),
            new AttendanceRequest(2, 2, "2024-09", "承認済")
        );
        when(attendanceRequestMapper.getAllRequests()).thenReturn(requests);

        // メソッドの実行
        List<AttendanceRequest> result = requestServiceImpl.getAllRequests();

        // 結果の検証
        assertNotNull(result, "結果がnullであってはいけません");
        assertEquals(2, result.size(), "結果のサイズが期待と異なります");
        assertEquals(requests, result, "結果が期待と異なります");
        verify(attendanceRequestMapper, times(1)).getAllRequests();
    }

    @Test
    @DisplayName("承認ステータスを正しく取得できること")
    void testGetApprovalStatus() {
        // テスト用データの準備
        String yearMonth = "2024-09";
        Integer employeeId = 1;
        String expectedStatus = "未提出";
        when(attendanceRequestMapper.getApprovalStatus(employeeId, yearMonth)).thenReturn(expectedStatus);

        // メソッドの実行
        String result = requestServiceImpl.getApprovalStatus(employeeId, yearMonth);

        // 結果の検証
        assertNotNull(result, "結果がnullであってはいけません");
        assertEquals(expectedStatus, result, "ステータスが期待と異なります");
        verify(attendanceRequestMapper, times(1)).getApprovalStatus(employeeId, yearMonth);
    }

    @Test
    @DisplayName("承認ステータスの更新が正しく行われること")
    void testUpdateApproveStatus() {
        Integer employeeId = 1;
        String yearMonth = "2024-09";
        String status = "承認済";

        // メソッドの実行
        requestServiceImpl.updateApproveStatus(employeeId, yearMonth, status);

        // verifyメソッドでメソッド呼び出しを確認
        verify(attendanceRequestMapper, times(1)).updateApproveStatus(employeeId, yearMonth, status);
    }

    @Test
    @DisplayName("承認申請の削除が正しく行われること")
    void testDeleteRequest() {
        Integer employeeId = 1;
        String yearMonth = "2024-09";

        // メソッドの実行
        requestServiceImpl.deleteRequest(employeeId, yearMonth);

        // verifyメソッドでメソッド呼び出しを確認
        verify(attendanceRequestMapper, times(1)).deleteRequest(employeeId, yearMonth);
    }
}
