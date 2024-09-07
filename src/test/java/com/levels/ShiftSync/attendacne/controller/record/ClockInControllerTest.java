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
    
    @Test
    @DisplayName("出勤時刻更新が成功すること")
    void testUpdateClockInTime_Success() throws Exception {
        // テスト用データを準備
        Integer recordId = 1;
        Integer employeeId = 123;
        String newClockInStr = "09:00";
        String currentClockInStr = "2023-09-01 08:00:00";
        Timestamp newClockInTimestamp = Timestamp.valueOf("2023-09-01 09:00:00");

        // モックの設定
        doNothing().when(clockInServiceImpl).update(recordId, employeeId, newClockInTimestamp);

        // モックMVCによるテスト実行
        mockMvc.perform(post("/update-clock-in-time")
                .param("recordId", recordId.toString())
                .param("employeeId", employeeId.toString())
                .param("newClockIn", newClockInStr)
                .param("currentClockIn", currentClockInStr))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-year-month"))
                .andExpect(flash().attribute("message", "出勤時刻を修正しました。"));

        // モックメソッドの呼び出し確認
        verify(clockInServiceImpl, times(1)).update(recordId, employeeId, newClockInTimestamp);
    }

    @Test
    @DisplayName("出勤時刻更新中に例外が発生した場合")
    void testUpdateClockInTime_ExceptionDuringUpdate() throws Exception {
        // テスト用データを準備
        Integer recordId = 1;
        Integer employeeId = 123;
        String newClockInStr = "09:00";
        String currentClockInStr = "2023-09-01 08:00:00";
        Timestamp newClockInTimestamp = Timestamp.valueOf("2023-09-01 09:00:00");

        // 例外をスローするようにモックの設定
        doThrow(new RuntimeException("Unexpected error"))
                .when(clockInServiceImpl).update(recordId, employeeId, newClockInTimestamp);

        // モックMVCによるテスト実行
        mockMvc.perform(post("/update-clock-in-time")
                .param("recordId", recordId.toString())
                .param("employeeId", employeeId.toString())
                .param("newClockIn", newClockInStr)
                .param("currentClockIn", currentClockInStr))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-year-month"))
                .andExpect(flash().attribute("message", "出勤時刻の修正に失敗しました。"));

        // モックメソッドの呼び出し確認
        verify(clockInServiceImpl, times(1)).update(recordId, employeeId, newClockInTimestamp);
    }

}
