package com.levels.ShiftSync.attendacne.controller.record;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.levels.ShiftSync.controller.attendance.EditClockTimeController;
import com.levels.ShiftSync.entity.AttendanceEditRequest;
import com.levels.ShiftSync.service.attendance.record.impl.EditClockTimeServiceImpl;

public class EditClockTimeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EditClockTimeServiceImpl editClockTimeServiceImpl;

    @Mock
    private Model model;

    @InjectMocks
    private EditClockTimeController editClockTimeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(editClockTimeController).build();
    }

    @Test
    @DisplayName("出勤・退勤時間の修正申請を正常に処理する")
    void testEditClockTime() throws Exception {
        // Arrange
        Integer recordId = 1;
        Integer employeeId = 123;
        String clockIn = "2024-09-28 08:00:00";
        String clockOut = "2024-09-28 17:00:00";
        String workDuration = "9:00:00";
        Integer selectedMonth = 9;

        when(editClockTimeServiceImpl.getCurrentEditRecord(employeeId, "2024-09-28")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/edit-clock-time")
                .param("recordId", String.valueOf(recordId))
                .param("employeeId", String.valueOf(employeeId))
                .param("clockIn", clockIn)
                .param("clockOut", clockOut)
                .param("workDuration", workDuration)
                .param("selectedMonth", String.valueOf(selectedMonth)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("attendance_record"))
                .andExpect(model().attributeExists("canEditTimeRequest"))
                .andExpect(view().name("attendance/record/edit-clock-time"));
    }

    @Test
    @DisplayName("既存の修正申請がある場合の出勤・退勤時間の修正申請処理")
    void testEditClockTimeWithExistingRequest() throws Exception {
        // Arrange
        Integer recordId = 1;
        Integer employeeId = 123;
        String clockIn = "2024-09-28 08:00:00";
        String clockOut = "2024-09-28 17:00:00";
        String workDuration = "9:00:00";
        Integer selectedMonth = 9;
        
        AttendanceEditRequest existingRequest = new AttendanceEditRequest();
        existingRequest.setClockIn(Timestamp.valueOf(clockIn));
        existingRequest.setClockOut(Timestamp.valueOf(clockOut));
        existingRequest.setWorkDuration(workDuration);

        when(editClockTimeServiceImpl.getCurrentEditRecord(employeeId, "2024-09-28")).thenReturn(existingRequest);

        // Act & Assert
        mockMvc.perform(post("/edit-clock-time")
                .param("recordId", String.valueOf(recordId))
                .param("employeeId", String.valueOf(employeeId))
                .param("clockIn", clockIn)
                .param("clockOut", clockOut)
                .param("workDuration", workDuration)
                .param("selectedMonth", String.valueOf(selectedMonth)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "申請済です。修正したい場合は管理者に取り下げをご依頼ください"))
                .andExpect(view().name("attendance/record/edit-clock-time"));
    }

    @Test
    @DisplayName("出勤・退勤時刻の修正申請を正常に処理する")
    void testUpdateClockTimes() throws Exception {
        // Arrange
        Integer recordId = 1;
        Integer employeeId = 123;
        String newClockInStr = "09:30";
        String newClockOutStr = "17:30";
        String currentClockInStr = "2024-09-28 08:00:00";
        String applicationReason = "修正理由";

        String datePart = "2024-09-28";
        doNothing().when(editClockTimeServiceImpl).editRequestClockInAndOut(recordId, employeeId, datePart,
                Timestamp.valueOf(datePart + " " + newClockInStr + ":00"),
                Timestamp.valueOf(datePart + " " + newClockOutStr + ":00"), applicationReason);

        // Act & Assert
        mockMvc.perform(post("/edit-clock-time-request")
                .param("recordId", String.valueOf(recordId))
                .param("employeeId", String.valueOf(employeeId))
                .param("newClockIn", newClockInStr)
                .param("newClockOut", newClockOutStr)
                .param("currentClockIn", currentClockInStr)
                .param("applicationReason", applicationReason))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "出勤・退勤時刻を修正申請をしました。"))
                .andExpect(view().name("attendance/record/edit-clock-time"));
    }

    @Test
    @DisplayName("出勤・退勤時刻の修正申請が失敗する場合")
    void testUpdateClockTimesFailure() throws Exception {
        // Arrange
        Integer recordId = 1;
        Integer employeeId = 123;
        String newClockInStr = "09:30";
        String newClockOutStr = "17:30";
        String currentClockInStr = "2024-09-28 08:00:00";
        String applicationReason = "修正理由";

        doThrow(new RuntimeException("Database error")).when(editClockTimeServiceImpl)
                .editRequestClockInAndOut(anyInt(), anyInt(), anyString(), any(), any(), anyString());

        // Act & Assert
        mockMvc.perform(post("/edit-clock-time-request")
                .param("recordId", String.valueOf(recordId))
                .param("employeeId", String.valueOf(employeeId))
                .param("newClockIn", newClockInStr)
                .param("newClockOut", newClockOutStr)
                .param("currentClockIn", currentClockInStr)
                .param("applicationReason", applicationReason))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "出勤・退勤時刻の修正申請に失敗しました。もう一度、ご対応をお願いいたします。"))
                .andExpect(view().name("attendance/record/edit-clock-time"));
    }
}

