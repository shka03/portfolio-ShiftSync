package com.levels.ShiftSync.attendacne.controller.approval;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.levels.ShiftSync.controller.approval.EditClockTimeRequestController;
import com.levels.ShiftSync.entity.AttendanceEditRequest;
import com.levels.ShiftSync.service.attendance.approval.impl.EditClockTimeRequestServiceImpl;

public class EditClockTimeRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EditClockTimeRequestServiceImpl editClockTimeRequestServiceImpl;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private EditClockTimeRequestController editClockTimeRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(editClockTimeRequestController).build();
    }

    @Test
    @DisplayName("出勤時刻修正申請のリストを正常に表示する")
    void testShowRequests() throws Exception {
        // Arrange
        List<AttendanceEditRequest> attendanceEditRequests = Arrays.asList(new AttendanceEditRequest(), new AttendanceEditRequest());
        when(editClockTimeRequestServiceImpl.getAllRequests()).thenReturn(attendanceEditRequests);

        // Act & Assert
        mockMvc.perform(get("/edit-clock-time-request-list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("attendance_time_corrections"))
                .andExpect(view().name("attendance/approval/edit-clock-time-request-list"));
    }

    @Test
    @DisplayName("特定の従業員の出勤時刻修正申請を正常に表示する")
    void testShowRequestsByEmployee() throws Exception {
        // Arrange
        Integer employeeId = 123;
        String yearMonthDay = "2024-09-28";
        List<AttendanceEditRequest> requestRecords = Arrays.asList(new AttendanceEditRequest(), new AttendanceEditRequest());
        when(editClockTimeRequestServiceImpl.getCurrentEditRecord(employeeId, yearMonthDay)).thenReturn(requestRecords);

        // Act & Assert
        mockMvc.perform(get("/edit-clock-time-request-list/{employeeId}/{yearMonthDay}", employeeId, yearMonthDay))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("attendance_time_corrections"))
                .andExpect(model().attribute("recordId", employeeId))
                .andExpect(model().attribute("yearMonthDay", yearMonthDay))
                .andExpect(view().name("attendance/approval/edit-clock-time-request-detail"));
    }

    @Test
    @DisplayName("特定の従業員の出勤時刻修正申請がない場合の処理")
    void testShowRequestsByEmployeeNoRecords() throws Exception {
        // Arrange
        Integer employeeId = 123;
        String yearMonthDay = "2024-09-28";
        when(editClockTimeRequestServiceImpl.getCurrentEditRecord(employeeId, yearMonthDay)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/edit-clock-time-request-list/{employeeId}/{yearMonthDay}", employeeId, yearMonthDay))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "対象データがありません"))
                .andExpect(view().name("attendance/approval/edit-clock-time-request-detail"));
    }

    @Test
    @DisplayName("出勤時刻修正申請の承認処理を正常に行う")
    void testUpdateApproveStatus() throws Exception {
        // Arrange
        Integer employeeId = 123;
        String yearMonthDay = "2024-09-28";
        AttendanceEditRequest request = new AttendanceEditRequest();
        request.setRecordId(1);
        request.setClockIn(Timestamp.valueOf("2024-09-28 08:00:00"));
        request.setClockOut(Timestamp.valueOf("2024-09-28 17:00:00"));
        request.setWorkDuration("9:00:00");
        List<AttendanceEditRequest> requestRecords = Collections.singletonList(request);

        when(editClockTimeRequestServiceImpl.getCurrentEditRecord(employeeId, yearMonthDay)).thenReturn(requestRecords);
        doNothing().when(editClockTimeRequestServiceImpl).updateEditClockTimeRecord(anyInt(), anyInt(), any(), any(), any());
        doNothing().when(editClockTimeRequestServiceImpl).deleteEditClockTimeApproval(anyInt(), anyString());

        // Act & Assert
        mockMvc.perform(post("/update-edit-clock-time-request")
                .param("employeeId", String.valueOf(employeeId))
                .param("yearMonthDay", yearMonthDay))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("approveMessage", "承認しました"))
                .andExpect(view().name("redirect:/edit-clock-time-request-list"));
    }

    @Test
    @DisplayName("出勤時刻修正申請を削除する処理を正常に行う")
    void testDeleteRequest() throws Exception {
        // Arrange
        Integer employeeId = 123;
        String yearMonthDay = "2024-09-28";
        doNothing().when(editClockTimeRequestServiceImpl).deleteEditClockTimeApproval(employeeId, yearMonthDay);

        // Act & Assert
        mockMvc.perform(post("/delete-edit-clock-time-request")
                .param("employeeId", String.valueOf(employeeId))
                .param("yearMonthDay", yearMonthDay))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("approveMessage", "申請を取り下げました。もう一度、申請してください。"))
                .andExpect(view().name("redirect:/edit-clock-time-request-list"));
    }
}

