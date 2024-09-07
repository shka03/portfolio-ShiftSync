package com.levels.ShiftSync.attendacne;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.levels.ShiftSync.controller.attendance.HomeController;
import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.LoginUser; // Import LoginUser
import com.levels.ShiftSync.service.attendance.record.impl.ApprovalServiceImpl;
import com.levels.ShiftSync.service.attendance.record.impl.WorkDurationServiceImpl;

public class HomeControllerTest {

    @Mock
    private WorkDurationServiceImpl workDurationServiceImpl;

    @Mock
    private ApprovalServiceImpl approvalServiceImpl;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

        // セキュリティコンテキストをモック
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        
        // LoginUser のモック作成
        LoginUser loginUser = mock(LoginUser.class);
        when(loginUser.getEmployeeId()).thenReturn(1);
        when(authentication.getPrincipal()).thenReturn(loginUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("今日の勤怠サマリーが正しく表示されること")
    void testShowHome_withAttendanceRecords() throws Exception {
        // テスト用データの準備
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Timestamp clockIn = Timestamp.valueOf(now.minusHours(2));
        Timestamp clockOut = Timestamp.valueOf(now.plusHours(1));

        AttendanceRecord record = new AttendanceRecord();
        record.setClockIn(clockIn);
        record.setClockOut(clockOut);

        List<AttendanceRecord> attendanceRecords = List.of(record);
        when(workDurationServiceImpl.getTodayRecordForEmployee()).thenReturn(attendanceRecords);
        when(approvalServiceImpl.hasRequestsForMonth(anyInt(), anyString())).thenReturn(false);
        when(approvalServiceImpl.isNoRequest(anyInt(), anyString())).thenReturn(true);

        // モックMVCによるテスト実行
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("attendance/home"))
               .andExpect(model().attribute("clockInTime", clockIn))
               .andExpect(model().attribute("clockOutTime", clockOut))
               .andExpect(model().attribute("canApproveRequest", true));
    }

    @Test
    @DisplayName("今日の出勤記録がない場合のサマリー表示")
    public void testShowHome_withoutAttendanceRecords() throws Exception {
        when(workDurationServiceImpl.getTodayRecordForEmployee()).thenReturn(List.of());
        when(approvalServiceImpl.hasRequestsForMonth(anyInt(), anyString())).thenReturn(false);
        when(approvalServiceImpl.isNoRequest(anyInt(), anyString())).thenReturn(true);

        // モックMVCによるテスト実行
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("attendance/home"))
               .andExpect(model().attributeExists("message", "canApproveRequest"))
               .andExpect(model().attribute("message", "本日の出勤記録がありません。"))
               .andExpect(model().attribute("canApproveRequest", true));
    }
}
