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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
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
                null))); // clockOut

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
        records.add(new AttendanceRecord(1, 1, now, null));
        when(attendanceRecordServiceImpl.getTodayAttendance()).thenReturn(records);

        mockMvc.perform(post("/clock-in"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attributeExists("clockInErrorMessage"))
               .andExpect(flash().attribute("clockInErrorMessage", "すでに出勤しています。"));

        verify(attendanceRecordServiceImpl, never()).clockInTime();
    }

    /**
     * 出勤時刻更新が正常に行われるテスト。
     * 正常なパラメータで出勤時刻を更新するか確認します。
     */
    @Test
    @DisplayName("出勤時刻更新が正常に行われるテスト")
    public void testUpdateClockInTime_Success() throws Exception {
        doNothing().when(attendanceRecordServiceImpl).updateClockInTime(any(), any(), any());

        mockMvc.perform(post("/updateClockInTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockIn", "08:30")
                .param("currentClockIn", "2024-08-22 08:00:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/yearly_attendance"));

        verify(attendanceRecordServiceImpl, times(1)).updateClockInTime(eq(1), eq(1), any(Timestamp.class));
    }

    /**
     * 無効な出勤時刻パラメータが提供された場合のテスト。
     * 出勤時刻のフォーマットが無効な場合、更新処理が行われないか確認します。
     */
    @Test
    @DisplayName("無効な出勤時刻パラメータが提供された場合、出勤時刻が更新されないテスト")
    public void testUpdateClockInTime_InvalidClockInFormat() throws Exception {
        mockMvc.perform(post("/updateClockInTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockIn", "invalid")
                .param("currentClockIn", "2024-08-22 08:00:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/yearly_attendance"));

        verify(attendanceRecordServiceImpl, never()).updateClockInTime(any(), any(), any());
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
        records.add(new AttendanceRecord(1, 1, now, null));
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
        records.add(new AttendanceRecord(1, 1, now, now));
        when(attendanceRecordServiceImpl.getTodayAttendance()).thenReturn(records);

        mockMvc.perform(post("/clock-out"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(flash().attributeExists("clockOutErrorMessage"))
               .andExpect(flash().attribute("clockOutErrorMessage", "すでに退勤しています。"));

        verify(attendanceRecordServiceImpl, never()).clockOutTime();
    }

    /**
     * 退勤時刻更新が正常に行われるテスト。
     * 正常なパラメータで退勤時刻を更新するか確認します。
     */
    @Test
    @DisplayName("退勤時刻更新が正常に行われるテスト")
    public void testUpdateClockOutTime_Success() throws Exception {
        doNothing().when(attendanceRecordServiceImpl).updateClockOutTime(any(), any(), any());

        mockMvc.perform(post("/updateClockOutTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockOut", "17:30")
                .param("currentClockOut", "2024-08-22 17:00:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/yearly_attendance"));

        verify(attendanceRecordServiceImpl, times(1)).updateClockOutTime(eq(1), eq(1), any(Timestamp.class));
    }

    /**
     * 無効な退勤時刻パラメータが提供された場合のテスト。
     * 退勤時刻のフォーマットが無効な場合、更新処理が行われないか確認します。
     */
    @Test
    @DisplayName("無効な退勤時刻パラメータが提供された場合、退勤時刻が更新されないテスト")
    public void testUpdateClockOutTime_InvalidClockOutFormat() throws Exception {
        mockMvc.perform(post("/updateClockOutTime")
                .param("recordId", "1")
                .param("employeeId", "1")
                .param("newClockOut", "invalid")
                .param("currentClockOut", "2024-08-22 17:00:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/yearly_attendance"));

        verify(attendanceRecordServiceImpl, never()).updateClockOutTime(any(), any(), any());
    }

    /**
     * 勤怠データが存在しない場合の月別勤怠リスト表示テスト。
     * 月のパラメータが指定されたが、データがない場合の処理を確認します。
     */
    @Test
    @DisplayName("勤怠データが存在しない月別勤怠リスト表示テスト")
    void testShowYearlyAttendance_NoData() throws Exception {
        when(attendanceRecordServiceImpl.getYearlyAttendanceForMonth(anyInt())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/yearly_attendance")
                .param("month", "8"))
                .andExpect(status().isOk())
                .andExpect(view().name("yearly_attendance"))
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

        mockMvc.perform(get("/yearly_attendance")
                .param("month", "8"))
                .andExpect(status().isOk())
                .andExpect(view().name("yearly_attendance"))
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

        mockMvc.perform(get("/yearly_attendance"))
                .andExpect(status().isOk())
                .andExpect(view().name("yearly_attendance"))
                .andExpect(model().attributeExists("attendance_records"))
                .andExpect(model().attribute("selectedMonth", LocalDate.now().getMonthValue()));
    }
}
