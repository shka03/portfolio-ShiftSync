package com.levels.ShiftSync.attendacne.controller.approval;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.levels.ShiftSync.controller.approval.RequestController;
import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.repository.attendance.record.RecordMapper;
import com.levels.ShiftSync.service.attendance.approval.impl.RequestServiceImpl;

public class RequestControllerTest {

    @Mock
    private RequestServiceImpl requestServiceImpl;

    @Mock
    private RecordMapper attendanceRecordMapper;

    @InjectMocks
    private RequestController requestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
    }

    @Test
    @DisplayName("全ての承認申請が正しく表示されること")
    void testShowRequests() throws Exception {
        // テスト用データの準備
        List<AttendanceRequest> requests = List.of(
            new AttendanceRequest(1, 1, "2024-09", "Pending"),
            new AttendanceRequest(2, 2, "2024-09", "Approved")
        );
        when(requestServiceImpl.getAllRequests()).thenReturn(requests);

        // モックMVCによるテスト実行
        mockMvc.perform(get("/attendance-requests-list"))
               .andExpect(status().isOk())
               .andExpect(view().name("attendance/approval/requests-list"))
               .andExpect(model().attribute("attendance_requests", requests));
    }

    @Test
    @DisplayName("従業員と年月に基づいた出退勤記録の表示が正しいこと")
    void testShowRequestsByEmployee_withRecords() throws Exception {
        // テスト用データの準備
        Integer employeeId = 1;
        String yearMonth = "2024-09";
        List<AttendanceRecord> records = List.of(
            new AttendanceRecord(1, 1, Timestamp.valueOf("2024-09-01 08:00:00"), Timestamp.valueOf("2024-09-01 17:00:00"), "")
        );
        String status = "Pending";

        when(attendanceRecordMapper.getRecordForYearByMonth(employeeId, yearMonth)).thenReturn(records);
        when(requestServiceImpl.getApprovalStatus(employeeId, yearMonth)).thenReturn(status);

        // モックMVCによるテスト実行
        mockMvc.perform(get("/attendance-approval/{employeeId}/{yearMonth}", employeeId, yearMonth))
               .andExpect(status().isOk())
               .andExpect(view().name("attendance/approval/detail"))
               .andExpect(model().attribute("attendance_records", records))
               .andExpect(model().attribute("employeeId", employeeId))
               .andExpect(model().attribute("yearMonth", yearMonth))
               .andExpect(model().attribute("status", status));
    }

    @Test
    @DisplayName("従業員と年月に基づいた出退勤記録がない場合の表示が正しいこと")
    void testShowRequestsByEmployee_withoutRecords() throws Exception {
        // テスト用データの準備
        Integer employeeId = 1;
        String yearMonth = "2024-09";
        when(attendanceRecordMapper.getRecordForYearByMonth(employeeId, yearMonth)).thenReturn(List.of());

        // モックMVCによるテスト実行
        mockMvc.perform(get("/attendance-approval/{employeeId}/{yearMonth}", employeeId, yearMonth))
               .andExpect(status().isOk())
               .andExpect(view().name("attendance/approval/detail"))
               .andExpect(model().attribute("message", "対象データがありません"));
    }

    @Test
    @DisplayName("承認ステータスの更新が正しくリダイレクトされること")
    void testUpdateApproveStatus() throws Exception {
        Integer employeeId = 1;
        String yearMonth = "2024-09";
        String status = "Approved";

        // モックMVCによるテスト実行
        mockMvc.perform(post("/update-approve-status")
                .param("employeeId", employeeId.toString())
                .param("yearMonth", yearMonth)
                .param("status", status))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/attendance-approval/" + employeeId + "/" + yearMonth));

        // メソッドの呼び出し確認
        verify(requestServiceImpl, times(1)).updateApproveStatus(employeeId, yearMonth, status);
    }

    @Test
    @DisplayName("承認申請の削除が正しくリダイレクトされること")
    void testDeleteRequest() throws Exception {
        Integer employeeId = 1;
        String yearMonth = "2024-09";

        // モックMVCによるテスト実行
        mockMvc.perform(post("/delete-request")
                .param("employeeId", employeeId.toString())
                .param("yearMonth", yearMonth))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/attendance-approval/" + employeeId + "/" + yearMonth));

        // メソッドの呼び出し確認
        verify(requestServiceImpl, times(1)).deleteRequest(employeeId, yearMonth);
    }
}
