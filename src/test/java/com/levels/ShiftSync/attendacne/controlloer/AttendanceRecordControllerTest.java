package com.levels.ShiftSync.attendacne.controlloer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.levels.ShiftSync.controller.AttendanceRecordController;
import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.impl.AttendanceRecordServiceImpl;

public class AttendanceRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AttendanceRecordServiceImpl attendanceRecordServiceImpl;

    @InjectMocks
    private AttendanceRecordController attendanceRecordController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(attendanceRecordController)
                .setViewResolvers(new InternalResourceViewResolver("/WEB-INF/views/", ".jsp"))
                .build();
    }

    /**
     * 出勤処理が成功するテスト。
     * 出勤記録がない状態で出勤処理を実行し、成功メッセージを表示するか確認します。
     */
    @Test
    @DisplayName("出勤処理が成功し、成功メッセージが表示されるテスト")
    void testClockInSuccess() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        when(attendanceRecordServiceImpl.getTodayAttendance())
            .thenReturn(new ArrayList<>()) // 出勤前には空のリスト
            .thenReturn(List.of(new AttendanceRecord(
                1, // employeeId
                null, now, // clockIn
                null, // clockOut
                null))); // workDuration

        mockMvc.perform(post("/clock-in"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"))
            .andExpect(flash().attributeExists("clockInSuccessMessage"))
            .andExpect(flash().attribute("clockInSuccessMessage", "おはようございます。出勤しました。"))
            .andExpect(flash().attribute("clockInTime", now));

        verify(attendanceRecordServiceImpl).clockInTime();
    }

    /**
     * 既に出勤中の場合の出勤処理テスト。
     * 出勤記録が既に存在する場合にエラーメッセージを表示するか確認します。
     */
    @Test
    @DisplayName("既に出勤中で出勤処理を実行した際にエラーメッセージが表示されるテスト")
    void testClockInAlreadyClockedIn() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<AttendanceRecord> records = new ArrayList<>();
        records.add(new AttendanceRecord(1, 1, now, null,null));
        when(attendanceRecordServiceImpl.getTodayAttendance()).thenReturn(records);

        mockMvc.perform(post("/clock-in"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attributeExists("clockInErrorMessage"))
               .andExpect(flash().attribute("clockInErrorMessage", "すでに出勤しています。"));

        verify(attendanceRecordServiceImpl, never()).clockInTime();
    }

    @Test
    @DisplayName("出勤時刻が正常に更新されるテスト")
    void testUpdateClockInTime_Success() throws Exception {
        // モックの設定
        Timestamp newClockIn = Timestamp.valueOf("2024-08-22 08:30:00");
        doNothing().when(attendanceRecordServiceImpl).updateClockInTime(anyInt(), anyInt(), any(Timestamp.class));

        // リクエストのシミュレーション
        mockMvc.perform(post("/updateClockInTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockIn", "08:30")
                .param("currentClockIn", "2024-08-22 08:00")
                .flashAttr("message", "出勤時刻を修正しました。"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-yearly"))
                .andExpect(flash().attribute("message", "出勤時刻を修正しました。"));

        // モックメソッドの呼び出し確認
        verify(attendanceRecordServiceImpl).updateClockInTime(1, 1, newClockIn);
    }

    @Test
    @DisplayName("無効な現在の出勤時刻形式でエラーメッセージが表示されるテスト")
    void testUpdateClockInTime_InvalidCurrentClockInFormat() throws Exception {
        // リクエストのシミュレーション
        mockMvc.perform(post("/updateClockInTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockIn", "08:30")
                .param("currentClockIn", "invalid_format"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-yearly"))
                .andExpect(flash().attribute("message", "現在の出勤時刻の形式が不正です。"));
    }

    @Test
    @DisplayName("無効な新しい出勤時刻形式でエラーメッセージが表示されるテスト")
    void testUpdateClockInTime_InvalidNewClockInFormat() throws Exception {
        // リクエストのシミュレーション
        mockMvc.perform(post("/updateClockInTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockIn", "invalid_time")
                .param("currentClockIn", "2024-08-22 08:00:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-yearly"))
                .andExpect(flash().attribute("message", "新しい出勤時刻の形式が不正です。"));
    }

    @Test
    @DisplayName("出勤時刻がnullの場合にエラーメッセージが表示されるテスト")
    void testUpdateClockInTime_NullParameters() throws Exception {
        // リクエストのシミュレーション
        mockMvc.perform(post("/updateClockInTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockIn", "")
                .param("currentClockIn", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-yearly"))
                .andExpect(flash().attribute("message", "出勤時刻が無効です。"));
    }

    @Test
    @DisplayName("出勤時刻更新中に例外が発生した場合のテスト")
    void testUpdateClockInTime_Exception() throws Exception {
        // モックの設定
        doThrow(new RuntimeException("DBエラー")).when(attendanceRecordServiceImpl).updateClockInTime(anyInt(), anyInt(), any(Timestamp.class));

        // リクエストのシミュレーション
        mockMvc.perform(post("/updateClockInTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockIn", "08:30")
                .param("currentClockIn", "2024-08-22 08:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-yearly"))
                .andExpect(flash().attribute("message", "出勤時刻の修正に失敗しました。"));
    }

    /**
     * 退勤処理が成功するテスト。
     * 出勤記録があり、退勤記録がない場合に退勤処理が成功するか確認します。
     */
    @Test
    @DisplayName("退勤処理が成功し、成功メッセージが表示されるテスト")
    void testClockOutSuccess() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<AttendanceRecord> records = new ArrayList<>();
        records.add(new AttendanceRecord(1, 1, now, null, null));
        when(attendanceRecordServiceImpl.getTodayAttendance()).thenReturn(records);

        mockMvc.perform(post("/clock-out"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attributeExists("clockOutSuccessMessage"))
               .andExpect(flash().attribute("clockOutSuccessMessage", "お疲れ様でした。退勤しました。"));

        verify(attendanceRecordServiceImpl).clockOutTime();
    }

    /**
     * 出勤記録がない場合の退勤処理テスト。
     * 出勤記録がない場合にエラーメッセージを表示するか確認します。
     */
    @Test
    @DisplayName("出勤記録がない場合の退勤処理でエラーメッセージが表示されるテスト")
    void testClockOutNoClockIn() throws Exception {
        when(attendanceRecordServiceImpl.getTodayAttendance()).thenReturn(new ArrayList<>());

        mockMvc.perform(post("/clock-out"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attributeExists("clockOutErrorMessage"))
               .andExpect(flash().attribute("clockOutErrorMessage", "出勤記録がありません。"));

        verify(attendanceRecordServiceImpl, never()).clockOutTime();
    }

    /**
     * 既に退勤済みの場合の退勤処理テスト。
     * 退勤記録が既に存在する場合にエラーメッセージを表示するか確認します。
     */
    @Test
    @DisplayName("既に退勤済みで退勤処理を実行した際にエラーメッセージが表示されるテスト")
    void testClockOutAlreadyClockedOut() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<AttendanceRecord> records = new ArrayList<>();
        records.add(new AttendanceRecord(1, 1, now, now, null));
        when(attendanceRecordServiceImpl.getTodayAttendance()).thenReturn(records);

        mockMvc.perform(post("/clock-out"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attributeExists("clockOutErrorMessage"))
               .andExpect(flash().attribute("clockOutErrorMessage", "すでに退勤しています。"));

        verify(attendanceRecordServiceImpl, never()).clockOutTime();
    }
    
    @Test
    @DisplayName("退勤時刻が正常に更新されるテスト")
    void testUpdateClockOutTime_Success() throws Exception {
        // モックの設定
        Timestamp newClockOut = Timestamp.valueOf("2024-08-22 17:30:00");
        doNothing().when(attendanceRecordServiceImpl).updateClockOutTime(anyInt(), anyInt(), any(Timestamp.class));

        // リクエストのシミュレーション
        mockMvc.perform(post("/updateClockOutTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockOut", "17:30")
                .param("currentClockOut", "2024-08-22 17:00")
                .flashAttr("message", "退勤時刻を修正しました。"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-yearly"))
                .andExpect(flash().attribute("message", "退勤時刻を修正しました。"));

        // モックメソッドの呼び出し確認
        verify(attendanceRecordServiceImpl).updateClockOutTime(1, 1, newClockOut);
    }

    @Test
    @DisplayName("無効な退勤時刻パラメータが提供された場合、退勤時刻が更新されないテスト")
    public void testUpdateClockOutTime_InvalidClockOutFormat() throws Exception {
        mockMvc.perform(post("/updateClockOutTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockOut", "invalid_format")
                .param("currentClockOut", "2024-08-22 17:00:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attendance-yearly"));

        verify(attendanceRecordServiceImpl, never()).updateClockOutTime(any(), any(), any());
    }

    @Test
    @DisplayName("退勤時刻が無効な場合にエラーメッセージが表示されるテスト")
    void testUpdateClockOutTime_InvalidClockOut() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/updateClockOutTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockOut", "")
                .param("currentClockOut", "2024-08-24 17:30:00"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/attendance-yearly"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", "退勤時刻が無効です。"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("現在の退勤時刻形式が不正な場合にエラーメッセージが表示されるテスト")
    void testUpdateClockOutTime_InvalidCurrentClockOutFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/updateClockOutTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockOut", "17:30") // 正しい形式の新しい退勤時刻
                .param("currentClockOut", "invalid_time")) // 不正な形式の現在の退勤時刻
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/attendance-yearly"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", "現在の退勤時刻の形式が不正です。"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("新しい退勤時刻形式が不正な場合にエラーメッセージが表示されるテスト")
    void testUpdateClockOutTime_InvalidNewClockOutFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/updateClockOutTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockOut", "invalid_time")
                .param("currentClockOut", "2024-08-24 17:30:00"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/attendance-yearly"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", "新しい退勤時刻の形式が不正です。"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("退勤時刻の修正に失敗した場合にエラーメッセージが表示されるテスト")
    void testUpdateClockOutTime_Failure() throws Exception {
        Mockito.doThrow(new RuntimeException("DB error")).when(attendanceRecordServiceImpl).updateClockOutTime(anyInt(), anyInt(), any(Timestamp.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/updateClockOutTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockOut", "17:30")
                .param("currentClockOut", "2024-08-24 17:30:00"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/attendance-yearly"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", "退勤時刻の修正に失敗しました。"))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 勤怠データが存在しない場合の月別勤怠リスト表示テスト。
     * 月のパラメータが指定されたが、データがない場合の処理を確認します。
     */
    @Test
    @DisplayName("勤怠データが存在しない月別勤怠リスト表示テスト")
    void testShowYearlyAttendance_NoData() throws Exception {
        when(attendanceRecordServiceImpl.getYearlyAttendanceForMonth(anyInt())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/attendance-yearly")
                .param("month", "8"))
                .andExpect(status().isOk())
                .andExpect(view().name("attendance-yearly"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "選択された月のデータはありません。"))
                .andExpect(model().attribute("selectedMonth", 8));
    }

    /**
     * 勤怠データが存在する場合の月別勤怠リスト表示テスト。
     * 月のパラメータが指定された場合にデータが表示されるか確認します。
     */
    @Test
    @DisplayName("勤怠データが存在する月別勤怠リスト表示テスト")
    void testShowYearlyAttendance_WithData() throws Exception {
        List<AttendanceRecord> attendanceRecords = new ArrayList<>();
        attendanceRecords.add(new AttendanceRecord(/* ここで必要なプロパティを設定 */));
        when(attendanceRecordServiceImpl.getYearlyAttendanceForMonth(anyInt())).thenReturn(attendanceRecords);

        mockMvc.perform(get("/attendance-yearly")
                .param("month", "8"))
                .andExpect(status().isOk())
                .andExpect(view().name("attendance-yearly"))
                .andExpect(model().attributeExists("attendance_records"))
                .andExpect(model().attribute("selectedMonth", 8));
    }

    /**
     * 月パラメータが指定されない場合に現在の月が使用されるテスト。
     * 月パラメータがnullの場合に現在の月のデータが表示されるか確認します。
     */
    @Test
    @DisplayName("月パラメータがnullの場合に現在の月が使用されるテスト")
    void testShowYearlyAttendance_DefaultMonth() throws Exception {
        List<AttendanceRecord> attendanceRecords = new ArrayList<>();
        attendanceRecords.add(new AttendanceRecord(/* ここで必要なプロパティを設定 */));
        when(attendanceRecordServiceImpl.getYearlyAttendanceForMonth(LocalDate.now().getMonthValue())).thenReturn(attendanceRecords);

        mockMvc.perform(get("/attendance-yearly"))
                .andExpect(status().isOk())
                .andExpect(view().name("attendance-yearly"))
                .andExpect(model().attributeExists("attendance_records"))
                .andExpect(model().attribute("selectedMonth", LocalDate.now().getMonthValue()));
    }
}
