package com.levels.ShiftSync.controller;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.impl.AttendanceRecordServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AttendanceRecordController {

    private final AttendanceRecordServiceImpl attendanceRecordServiceImpl;

    /**
     * 今日の勤怠情報を表示するメソッド
     * @param model Thymeleafテンプレートにデータを渡すためのモデル
     * @return "attendance" テンプレート名
     */
    @GetMapping
    public String showAttendancePage(Model model) {
        List<AttendanceRecord> todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();
        populateAttendanceModel(model, todayAttendance);
        return "attendance";
    }

    /**
     * 出勤処理を行うメソッド
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return リダイレクト先のURL
     */
    @PostMapping("/clock-in")
    public String clockIn(RedirectAttributes attributes) {
        List<AttendanceRecord> todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();

        if (!todayAttendance.isEmpty()) {
            handleAlreadyClockedIn(attributes, todayAttendance.get(0).getClockIn());
            return "redirect:/";
        }

        attendanceRecordServiceImpl.clockInTime();
        todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();
        addClockInSuccessAttributes(attributes, todayAttendance.get(0).getClockIn());
        return "redirect:/";
    }

    /**
     * 退勤処理を行うメソッド
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return リダイレクト先のURL
     */
    @PostMapping("/clock-out")
    public String clockOutTime(RedirectAttributes attributes) {
        List<AttendanceRecord> todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();

        if (todayAttendance.isEmpty()) {
            addClockOutErrorAttributes(attributes, "出勤記録がありません。");
            return "redirect:/";
        }

        AttendanceRecord record = todayAttendance.get(0);
        if (record.getClockOut() != null) {
            addClockOutErrorAttributes(attributes, "すでに退勤しています。");
            return "redirect:/";
        }

        attendanceRecordServiceImpl.clockOutTime();
        todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();
        addClockOutSuccessAttributes(attributes, todayAttendance.get(0).getClockOut());
        return "redirect:/";
    }

    /**
     * 月間勤怠情報を表示するメソッド
     * @param model Thymeleafテンプレートにデータを渡すためのモデル
     * @return "monthly_attendance" テンプレート名
     */
    @GetMapping("/monthly_attendance")
    public String showMonthlyAttendancePage(Model model) {
        List<AttendanceRecord> monthlyAttendance = attendanceRecordServiceImpl.getMonthlyAttendance();

        if (isMonthlyAttendanceEmpty(monthlyAttendance)) {
            addEmptyAttendanceMessage(model);
            return "monthly_attendance";
        }

        addAttendanceRecordsToModel(model, monthlyAttendance);
        return "monthly_attendance";
    }

    /**
     * 今日の勤怠情報をModelに設定するメソッド
     * @param model Modelにデータを追加するためのオブジェクト
     * @param todayAttendance 今日の勤怠記録のリスト
     */
    private void populateAttendanceModel(Model model, List<AttendanceRecord> todayAttendance) {
        if (!todayAttendance.isEmpty()) {
            AttendanceRecord record = todayAttendance.get(0);
            model.addAttribute("clockInTime", record.getClockIn());
            model.addAttribute("clockOutTime", record.getClockOut());
        } else {
            model.addAttribute("clockInTime", null);
            model.addAttribute("clockOutTime", null);
        }
    }

    /**
     * すでに出勤している場合の処理
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @param clockInTime 出勤時刻
     */
    private void handleAlreadyClockedIn(RedirectAttributes attributes, Timestamp clockInTime) {
        addClockInErrorAttributes(attributes, clockInTime);
    }

    /**
     * 出勤成功メッセージをFlashAttributesに追加するメソッド
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @param clockInTime 出勤時刻
     */
    private void addClockInSuccessAttributes(RedirectAttributes attributes, Timestamp clockInTime) {
        attributes.addFlashAttribute("clockInTime", clockInTime);
        attributes.addFlashAttribute("clockInSuccessMessage", "おはようございます。出勤しました。");
    }

    /**
     * 出勤エラーメッセージをFlashAttributesに追加するメソッド
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @param clockInTime 出勤時刻
     */
    private void addClockInErrorAttributes(RedirectAttributes attributes, Timestamp clockInTime) {
        attributes.addFlashAttribute("clockInTime", clockInTime);
        attributes.addFlashAttribute("clockInErrorMessage", "すでに出勤してます。");
    }

    /**
     * 退勤エラーメッセージをFlashAttributesに追加するメソッド
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @param message エラーメッセージ
     */
    private void addClockOutErrorAttributes(RedirectAttributes attributes, String message) {
        attributes.addFlashAttribute("clockOutErrorMessage", message);
    }

    /**
     * 退勤成功メッセージをFlashAttributesに追加するメソッド
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @param clockOutTime 退勤時刻
     */
    private void addClockOutSuccessAttributes(RedirectAttributes attributes, Timestamp clockOutTime) {
        attributes.addFlashAttribute("clockOutTime", clockOutTime);
        attributes.addFlashAttribute("clockOutSuccessMessage", "お疲れ様でした。退勤しました。");
    }

    /**
     * 月間勤怠データが空かどうかをチェックするメソッド
     * @param monthlyAttendance 月間勤怠記録のリスト
     * @return 月間勤怠データが空の場合はtrue、それ以外はfalse
     */
    private boolean isMonthlyAttendanceEmpty(List<AttendanceRecord> monthlyAttendance) {
        return monthlyAttendance == null || monthlyAttendance.isEmpty();
    }

    /**
     * 空の勤怠データメッセージをModelに追加するメソッド
     * @param model Modelにデータを追加するためのオブジェクト
     */
    private void addEmptyAttendanceMessage(Model model) {
        model.addAttribute("message", "今月の勤怠データはまだありません。");
    }

    /**
     * 勤怠データをModelに追加するメソッド
     * @param model Modelにデータを追加するためのオブジェクト
     * @param monthlyAttendance 月間勤怠記録のリスト
     */
    private void addAttendanceRecordsToModel(Model model, List<AttendanceRecord> monthlyAttendance) {
        model.addAttribute("attendance_records", monthlyAttendance);
    }
}
