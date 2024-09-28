package com.levels.ShiftSync.attendacne.controller.record;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.levels.ShiftSync.controller.attendance.ClockInController;
import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.attendance.record.RecordService;
import com.levels.ShiftSync.service.attendance.record.impl.WorkDurationServiceImpl;

public class ClockInControllerTest {

    @Mock
    private RecordService clockInServiceImpl;

    @Mock
    private WorkDurationServiceImpl workDurationServiceImpl;

    @InjectMocks
    private ClockInController clockInController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clockInController).build();
    }

    @Test
    @DisplayName("出勤処理が正常に行われること")
    void testClockIn_Success() throws Exception {
    	// テスト用データを準備
        Timestamp now = new Timestamp(System.currentTimeMillis());
        when(workDurationServiceImpl.getTodayRecordForEmployee())
            .thenReturn(new ArrayList<>()) // 出勤前には空のリスト
            .thenReturn(List.of(new AttendanceRecord(
                1, // recordId
                null, // employeeId 
                now, // clockIn
                null, // clockOut
                null))); // workDuration
        doNothing().when(clockInServiceImpl).insert();; // voidメソッドには doNothing() を使用

        // モックMVCによるテスト実行
        mockMvc.perform(post("/clock-in"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attribute("clockInTime", now))
               .andExpect(flash().attribute("clockInSuccessMessage", "おはようございます。出勤しました。"));

        // モックメソッドの呼び出し確認
        verify(clockInServiceImpl, times(1)).insert();;
        verify(workDurationServiceImpl, times(2)).getTodayRecordForEmployee(); // 最初と2回目の呼び出し
    }

    @Test
    @DisplayName("すでに出勤している場合の処理")
    void testClockIn_AlreadyClockedIn() throws Exception {
        // テスト用データの準備
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Timestamp clockIn = Timestamp.valueOf(now);

        // 既に出勤しているレコードを含むリストを返すモック設定
        AttendanceRecord record = new AttendanceRecord();
        record.setClockIn(clockIn);
        when(workDurationServiceImpl.getTodayRecordForEmployee()).thenReturn(List.of(record));

        // モックMVCによるテスト実行
        mockMvc.perform(post("/clock-in"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attribute("clockInTime", clockIn))
               .andExpect(flash().attribute("clockInErrorMessage", "すでに出勤しています。"));

        // voidメソッドは呼び出されていないことを確認
        verify(clockInServiceImpl, times(0)).insert();;
        verify(workDurationServiceImpl, times(1)).getTodayRecordForEmployee();
    }

}
