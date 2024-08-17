package com.levels.ShiftSync.attendacne.controlloer;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.levels.ShiftSync.controller.AttendanceRecordController;
import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.impl.AttendanceRecordServiceImpl;

public class AttendanceRecordControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AttendanceRecordServiceImpl attendanceRecordServiceImpl;

    @InjectMocks
    private AttendanceRecordController attendanceRecordController;

    @BeforeEach
    void setUp() {
        // Mockitoの初期化
        MockitoAnnotations.openMocks(this);
        // MockMvcのセットアップ
        mockMvc = MockMvcBuilders.standaloneSetup(attendanceRecordController).build();
    }

    /**
     * 出勤処理が正常に行われる場合のテスト。
     * 出勤記録がない状態で新たに出勤処理を実行し、成功メッセージを表示するかを検証します。
     */
    @Test
    void testClockInSuccess() throws Exception {
        // 1回の出勤登録を実行
    	Timestamp now = new Timestamp(System.currentTimeMillis());
        when(attendanceRecordServiceImpl.getTodayAttendance())
        .thenReturn(new ArrayList<>()) // 出勤前には空のリスト
        .thenReturn(List.of(new AttendanceRecord( // 出勤後には出勤記録
            1, // employeeId
            null, now, // clockIn
            null))); // clockOut

        // 出勤処理を実行
        mockMvc.perform(post("/clock-in"))
            .andExpect(status().is3xxRedirection()) // リダイレクトのステータスコードを確認
            .andExpect(redirectedUrl("/")) // リダイレクト先のURLを確認
            .andExpect(flash().attributeExists("clockInSuccessMessage")) // 成功メッセージが存在することを確認
            .andExpect(flash().attribute("clockInSuccessMessage", "おはようございます。出勤しました。")) // 成功メッセージの内容を確認
            .andExpect(flash().attribute("clockInTime", now)); // 出勤時刻が正しく設定されているか確認

        // 出勤処理メソッドが呼び出されたことを確認
        verify(attendanceRecordServiceImpl).clockInTime();
    }

    /**
     * 既に出勤中の場合の出勤処理テスト。
     * 出勤記録が既に存在する場合にエラーメッセージを表示するかを検証します。
     */
    @Test
    void testClockInAlreadyClockedIn() throws Exception {
        // 出勤記録が既に存在する場合の動作を模擬
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<AttendanceRecord> records = new ArrayList<>();
        records.add(new AttendanceRecord(1, 1, now, null));
        when(attendanceRecordServiceImpl.getTodayAttendance()).thenReturn(records);

        // 出勤処理を実行し、エラーメッセージを検証
        mockMvc.perform(post("/clock-in"))
               .andExpect(status().is3xxRedirection()) // リダイレクトのステータスコードを確認
               .andExpect(redirectedUrl("/")) // リダイレクト先のURLを確認
               .andExpect(flash().attributeExists("clockInErrorMessage")) // エラーメッセージが存在することを確認
               .andExpect(flash().attribute("clockInErrorMessage", "すでに出勤してます。")); // エラーメッセージの内容を確認

        // 出勤処理メソッドが呼び出されなかったことを確認
        verify(attendanceRecordServiceImpl, never()).clockInTime();
    }

    /**
     * 退勤処理が正常に行われる場合のテスト。
     * 出勤記録があり、退勤記録がない場合に退勤処理が成功するかを検証します。
     */
    @Test
    void testClockOutSuccess() throws Exception {
        // 出勤記録があり、退勤記録がない場合の動作を模擬
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<AttendanceRecord> records = new ArrayList<>();
        records.add(new AttendanceRecord(1, 1, now, null));
        when(attendanceRecordServiceImpl.getTodayAttendance()).thenReturn(records);

        // 退勤処理を実行
        mockMvc.perform(post("/clock-out"))
               .andExpect(status().is3xxRedirection()) // リダイレクトのステータスコードを確認
               .andExpect(redirectedUrl("/")) // リダイレクト先のURLを確認
               .andExpect(flash().attributeExists("clockOutSuccessMessage")) // 成功メッセージが存在することを確認
               .andExpect(flash().attribute("clockOutSuccessMessage", "お疲れ様でした。退勤しました。")); // 成功メッセージの内容を確認

        // 退勤処理メソッドが呼び出されたことを確認
        verify(attendanceRecordServiceImpl).clockOutTime();
    }

    /**
     * 退勤処理が出勤記録がない場合に行われた場合のテスト。
     * 出勤記録がない場合にエラーメッセージを表示するかを検証します。
     */
    @Test
    void testClockOutNoClockIn() throws Exception {
        // 出勤記録が存在しない場合の動作を模擬
        when(attendanceRecordServiceImpl.getTodayAttendance()).thenReturn(new ArrayList<>());

        // 退勤処理を実行し、エラーメッセージを検証
        mockMvc.perform(post("/clock-out"))
               .andExpect(status().is3xxRedirection()) // リダイレクトのステータスコードを確認
               .andExpect(redirectedUrl("/")) // リダイレクト先のURLを確認
               .andExpect(flash().attributeExists("clockOutErrorMessage")) // エラーメッセージが存在することを確認
               .andExpect(flash().attribute("clockOutErrorMessage", "出勤記録がありません。")); // エラーメッセージの内容を確認

        // 退勤処理メソッドが呼び出されなかったことを確認
        verify(attendanceRecordServiceImpl, never()).clockOutTime();
    }

    /**
     * 退勤処理が既に退勤済みの場合に行われた場合のテスト。
     * 退勤記録が既に存在する場合にエラーメッセージを表示するかを検証します。
     */
    @Test
    void testClockOutAlreadyClockedOut() throws Exception {
        // 出勤記録があり、既に退勤済みの場合の動作を模擬
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<AttendanceRecord> records = new ArrayList<>();
        records.add(new AttendanceRecord(1, 1, now, now));
        when(attendanceRecordServiceImpl.getTodayAttendance()).thenReturn(records);

        // 退勤処理を実行し、エラーメッセージを検証
        mockMvc.perform(post("/clock-out"))
               .andExpect(status().is3xxRedirection()) // リダイレクトのステータスコードを確認
               .andExpect(redirectedUrl("/")) // リダイレクト先のURLを確認
               .andExpect(flash().attributeExists("clockOutErrorMessage")) // エラーメッセージが存在することを確認
               .andExpect(flash().attribute("clockOutErrorMessage", "すでに退勤しています。")); // エラーメッセージの内容を確認

        // 退勤処理メソッドが呼び出されなかったことを確認
        verify(attendanceRecordServiceImpl, never()).clockOutTime();
    }
}
