package com.levels.ShiftSync.attendacne.controller.record;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.levels.ShiftSync.controller.attendance.ClockOutController;
import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.attendance.record.RecordService;
import com.levels.ShiftSync.service.attendance.record.impl.WorkDurationServiceImpl;

public class ClockOutControllerTest {

    @Mock
    private RecordService clockOutServiceImpl;

    @Mock
    private WorkDurationServiceImpl workDurationServiceImpl;

    @InjectMocks
    private ClockOutController clockOutController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clockOutController).build();
    }

    @Test
    @DisplayName("退勤処理が正常に行われること")
    void testClockOut_Success() throws Exception {
        // テスト用データの準備
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Timestamp clockOut = Timestamp.valueOf(now);

        // 出勤記録が存在し、退勤記録がない場合
        AttendanceRecord record = new AttendanceRecord();
        record.setClockOut(null);  // 退勤記録がない

        // モック設定
        when(workDurationServiceImpl.getTodayRecordForEmployee()).thenReturn(List.of(record));

        // `clockOutTime` メソッドが正しい時刻を返すようにモックを設定
        doAnswer(invocation -> {
            // 退勤処理後に `clockOut` を設定する
            record.setClockOut(clockOut);
            return null;
        }).when(clockOutServiceImpl).insert();
        
        when(workDurationServiceImpl.getTodayRecordForEmployee()).thenReturn(List.of(record));
        doNothing().when(workDurationServiceImpl).upsertTodayWorkDuration(); // voidメソッドには doNothing() を使用

        // モックMVCによるテスト実行
        mockMvc.perform(post("/clock-out"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attribute("clockOutTime", clockOut))
               .andExpect(flash().attribute("clockOutSuccessMessage", "お疲れ様でした。退勤しました。"));

        // モックメソッドの呼び出し確認
        verify(clockOutServiceImpl, times(1)).insert();
        verify(workDurationServiceImpl, times(2)).getTodayRecordForEmployee(); // 最初と2回目の呼び出し
        verify(workDurationServiceImpl, times(1)).upsertTodayWorkDuration(); // upsertTodayWorkDurationが呼び出されていることを確認
    }

    @Test
    @DisplayName("出勤記録がない場合")
    void testClockOut_NoAttendanceRecord() throws Exception {
        // 出勤記録がない場合
        when(workDurationServiceImpl.getTodayRecordForEmployee()).thenReturn(List.of());

        // モックMVCによるテスト実行
        mockMvc.perform(post("/clock-out"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attribute("clockOutErrorMessage", "出勤記録がありません。"));

        // モックメソッドの呼び出し確認
        verify(clockOutServiceImpl, times(0)).insert();
        verify(workDurationServiceImpl, times(1)).getTodayRecordForEmployee();
    }

    @Test
    @DisplayName("すでに退勤している場合")
    void testClockOut_AlreadyClockedOut() throws Exception {
        // 出勤記録が存在し、すでに退勤済みの場合
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Timestamp clockOut = Timestamp.valueOf(now);

        AttendanceRecord record = new AttendanceRecord();
        record.setClockOut(clockOut);  // すでに退勤済み
        when(workDurationServiceImpl.getTodayRecordForEmployee()).thenReturn(List.of(record));

        // モックMVCによるテスト実行
        mockMvc.perform(post("/clock-out"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attribute("clockOutErrorMessage", "すでに退勤しています。"));

        // モックメソッドの呼び出し確認
        verify(clockOutServiceImpl, times(0)).insert();
        verify(workDurationServiceImpl, times(1)).getTodayRecordForEmployee();
    }
    
    @Test
    @DisplayName("退勤時刻の更新が正常に行われること")
    void testUpdateClockOutTime_Success() throws Exception {
        // テスト用データの準備
        Integer recordId = 1;
        Integer employeeId = 123;
        String newClockOutStr = "18:00";
        String currentClockOutStr = "2024-09-01 17:00:00";
        Timestamp newClockOut = Timestamp.valueOf("2024-09-01 18:00:00");
        Integer month = 9;

        // モック設定
        doNothing().when(clockOutServiceImpl).update(recordId, employeeId, newClockOut);

        // モックMVCによるテスト実行
        mockMvc.perform(post("/update-clock-out-time")
                .param("recordId", recordId.toString())
                .param("employeeId", employeeId.toString())
                .param("newClockOut", newClockOutStr)
                .param("currentClockOut", currentClockOutStr)
                .param("month", month.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-year-month?month=9"))
                .andExpect(flash().attribute("message", "退勤時刻を修正しました。"));

        // モックメソッドの呼び出し確認
        verify(clockOutServiceImpl, times(1)).update(recordId, employeeId, newClockOut);
    }

    @Test
    @DisplayName("退勤時刻の修正に失敗した場合")
    void testUpdateClockOutTime_Failure() throws Exception {
        // テスト用データの準備
        Integer recordId = 1;
        Integer employeeId = 123;
        String newClockOutStr = "18:00";
        String currentClockOutStr = "2024-09-01 17:00:00";
        Timestamp newClockOut = Timestamp.valueOf("2024-09-01 18:00:00");
        Integer month = 9;

        // モック設定：修正に失敗する例外をスロー
        doThrow(new RuntimeException("修正エラー")).when(clockOutServiceImpl).update(recordId, employeeId, newClockOut);

        // モックMVCによるテスト実行
        mockMvc.perform(post("/update-clock-out-time")
                .param("recordId", recordId.toString())
                .param("employeeId", employeeId.toString())
                .param("newClockOut", newClockOutStr)
                .param("currentClockOut", currentClockOutStr)
                .param("month", month.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-year-month"))
                .andExpect(flash().attribute("message", "退勤時刻の修正に失敗しました。"));

        // モックメソッドの呼び出し確認
        verify(clockOutServiceImpl, times(1)).update(recordId, employeeId, newClockOut);
    }

}
