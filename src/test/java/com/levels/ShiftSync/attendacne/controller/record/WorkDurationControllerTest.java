package com.levels.ShiftSync.attendacne.controller.record;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.levels.ShiftSync.controller.attendance.WorkDurationController;
import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.LoginUser;
import com.levels.ShiftSync.service.attendance.approval.impl.ApprovalServiceImpl;
import com.levels.ShiftSync.service.attendance.record.impl.CheckServiceImpl;
import com.levels.ShiftSync.service.attendance.record.impl.WorkDurationServiceImpl;
import com.levels.ShiftSync.service.impl.CsvExportServiceImpl;

public class WorkDurationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ApprovalServiceImpl approvalServiceImpl;

    @Mock
    private CsvExportServiceImpl csvExportServiceImpl;

    @Mock
    private CheckServiceImpl recordCheckServiceImpl;

    @Mock
    private WorkDurationServiceImpl workDurationServiceImpl;

    @InjectMocks
    private WorkDurationController workDurationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(workDurationController)
                .setViewResolvers(new InternalResourceViewResolver("/WEB-INF/views/", ".jsp"))
                .build();

        // テスト用のセキュリティコンテキストの設定
        LoginUser loginUser = new LoginUser(1, "testuser", "password", Collections.emptyList());
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("勤怠履歴が正常に表示されるかのテスト - 月がnullの場合")
    void testShowYearMonth_NullMonth() throws Exception {
        // 初期表示時に月がnullの場合をテスト
        List<AttendanceRecord> recordsForNullMonth = List.of(new AttendanceRecord(1, 1, Timestamp.valueOf("2024-09-01 09:00:00"), null, "8:00"));
        when(workDurationServiceImpl.getRecordForYearByMonth(LocalDate.now().getMonthValue())).thenReturn(recordsForNullMonth);
        when(approvalServiceImpl.hasRequestsForMonth(anyInt(), anyString())).thenReturn(true);
        when(approvalServiceImpl.isNoRequest(anyInt(), anyString())).thenReturn(true);

        mockMvc.perform(get("/attendance-year-month"))
                .andExpect(status().isOk())
                .andExpect(view().name("attendance/record/year-month-detail"))
                .andExpect(model().attributeExists("attendance_records", "selectedMonth", "canApproveRequest"))
                .andExpect(model().attribute("attendance_records", recordsForNullMonth))
                .andExpect(model().attribute("selectedMonth", LocalDate.now().getMonthValue()))
                .andExpect(model().attribute("canApproveRequest", false));

        // モックの呼び出し確認
        verify(workDurationServiceImpl, times(1)).getRecordForYearByMonth(LocalDate.now().getMonthValue());
        verify(approvalServiceImpl, times(1)).hasRequestsForMonth(anyInt(), anyString());
        verify(approvalServiceImpl, times(1)).isNoRequest(anyInt(), anyString());
    }

    @Test
    @DisplayName("勤怠履歴が正常に表示されるかのテスト - 特定の月が指定された場合")
    void testShowYearMonth_SpecificMonth() throws Exception {
        // 特定の月が指定された場合のテスト
        List<AttendanceRecord> recordsForSpecificMonth = List.of(new AttendanceRecord(1, 1, Timestamp.valueOf("2024-09-01 09:00:00"), null, "8:00"));
        when(workDurationServiceImpl.getRecordForYearByMonth(9)).thenReturn(recordsForSpecificMonth);
        when(approvalServiceImpl.hasRequestsForMonth(anyInt(), anyString())).thenReturn(true);
        when(approvalServiceImpl.isNoRequest(anyInt(), anyString())).thenReturn(true);

        mockMvc.perform(get("/attendance-year-month")
                .param("month", "9"))
                .andExpect(status().isOk())
                .andExpect(view().name("attendance/record/year-month-detail"))
                .andExpect(model().attributeExists("attendance_records", "selectedMonth", "canApproveRequest"))
                .andExpect(model().attribute("attendance_records", recordsForSpecificMonth))
                .andExpect(model().attribute("selectedMonth", 9))
                .andExpect(model().attribute("canApproveRequest", false));

        // モックの呼び出し確認
        verify(workDurationServiceImpl, times(1)).getRecordForYearByMonth(9);
        verify(approvalServiceImpl, times(1)).hasRequestsForMonth(anyInt(), anyString());
        verify(approvalServiceImpl, times(1)).isNoRequest(anyInt(), anyString());
    }


    @Test
    @DisplayName("勤怠履歴がない場合にメッセージを表示するテスト")
    void testShowYearMonth_NoRecords() throws Exception {
        when(workDurationServiceImpl.getRecordForYearByMonth(anyInt())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/attendance-year-month")
                .param("month", "9"))
                .andExpect(status().isOk())
                .andExpect(view().name("attendance/record/year-month-detail"))
                .andExpect(model().attributeExists("message", "selectedMonth"))
                .andExpect(model().attribute("message", "選択された月のデータはありません。"))
                .andExpect(model().attribute("selectedMonth", 9));

        verify(workDurationServiceImpl).getRecordForYearByMonth(9);
    }

    @Test
    @DisplayName("CSVダウンロードが正常に動作するテスト")
    void testDownloadCsv_Success() throws Exception {
        String csvData = "Record ID,Employee ID,Clock In,Clock Out,Work Duration\n1,1,2024-09-01 09:00:00,,8:00";
        List<AttendanceRecord> records = List.of(new AttendanceRecord(1, 1, Timestamp.valueOf("2024-09-01 09:00:00"), null, "8:00"));
        when(workDurationServiceImpl.getRecordForYearByMonth(anyInt())).thenReturn(records);
        when(csvExportServiceImpl.exportToCsv(records)).thenReturn(csvData);

        mockMvc.perform(get("/download-csv")
                .param("month", "9"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_records_9.csv"))
                .andExpect(content().string(csvData));

        verify(workDurationServiceImpl).getRecordForYearByMonth(9);
        verify(csvExportServiceImpl).exportToCsv(records);
    }

    @Test
    @DisplayName("承認申請処理が正常に行われるテスト")
    void testInsertApproveRequest_Success() throws Exception {
        when(recordCheckServiceImpl.hasRecordsForMonth(anyInt(), anyString())).thenReturn(true);

        mockMvc.perform(post("/insert-approve-request")
                .param("month", "9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-year-month?month=9"))
                .andExpect(flash().attribute("approveSuccessMessage", "承認申請をしました。"));

        verify(recordCheckServiceImpl).hasRecordsForMonth(anyInt(), anyString());
        verify(approvalServiceImpl).insertApproveRequest(anyInt(), anyString());
    }

    @Test
    @DisplayName("承認申請時に勤怠記録がない場合のエラーメッセージ表示テスト")
    void testInsertApproveRequest_NoRecords() throws Exception {
        when(recordCheckServiceImpl.hasRecordsForMonth(anyInt(), anyString())).thenReturn(false);

        mockMvc.perform(post("/insert-approve-request")
                .param("month", "9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-year-month?month=9"))
                .andExpect(flash().attribute("approveErrorMessage", "指定された年月に勤怠履歴が存在しないため、承認申請ができません。"));

        verify(recordCheckServiceImpl).hasRecordsForMonth(anyInt(), anyString());
        verify(approvalServiceImpl, never()).insertApproveRequest(anyInt(), anyString());
    }
}

