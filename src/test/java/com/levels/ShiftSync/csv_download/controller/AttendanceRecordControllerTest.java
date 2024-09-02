package com.levels.ShiftSync.csv_download.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.levels.ShiftSync.controller.AttendanceRecordController;
import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.impl.AttendanceRecordServiceImpl;
import com.levels.ShiftSync.service.impl.CsvExportServiceImpl;

@WebMvcTest(controllers = AttendanceRecordController.class)
public class AttendanceRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttendanceRecordServiceImpl attendanceRecordServiceImpl;

    @MockBean
    private CsvExportServiceImpl csvExportServiceImpl;

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testDownloadCsv_WithMonth() throws Exception {
        // Mockデータの準備
        List<AttendanceRecord> records = Collections.singletonList(new AttendanceRecord(1, 1, Timestamp.valueOf("2024-08-24 08:00:00"), Timestamp.valueOf("2024-08-24 17:00:00"), null));
        when(attendanceRecordServiceImpl.getRecordForYearByMonth(8)).thenReturn(records);
        when(csvExportServiceImpl.exportToCsv(records)).thenReturn("Record ID,Employee ID,Clock In,Clock Out\n1,1,2024-08-24 08:00:00,2024-08-24 17:00:00");

        // リクエストの送信とレスポンスの検証
        mockMvc.perform(get("/download-csv").param("month", "8"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_records_8.csv"))
                .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
                .andExpect(content().string("Record ID,Employee ID,Clock In,Clock Out\n1,1,2024-08-24 08:00:00,2024-08-24 17:00:00"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testDownloadCsv_WithoutMonth() throws Exception {
        // Mockデータの準備
        List<AttendanceRecord> records = Collections.singletonList(new AttendanceRecord(1, 1, Timestamp.valueOf("2024-08-24 08:00:00"), Timestamp.valueOf("2024-08-24 17:00:00"), null));
        when(attendanceRecordServiceImpl.getRecordForYearByMonth(LocalDate.now().getMonthValue())).thenReturn(records);
        when(csvExportServiceImpl.exportToCsv(records)).thenReturn("Record ID,Employee ID,Clock In,Clock Out\n1,1,2024-08-24 08:00:00,2024-08-24 17:00:00");

        // リクエストの送信とレスポンスの検証
        mockMvc.perform(get("/download-csv"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_records_" + LocalDate.now().getMonthValue() + ".csv"))
                .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
                .andExpect(content().string("Record ID,Employee ID,Clock In,Clock Out\n1,1,2024-08-24 08:00:00,2024-08-24 17:00:00"));
    }
}
