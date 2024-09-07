package com.levels.ShiftSync.controller.attendance;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.attendance.record.RecordService;
import com.levels.ShiftSync.service.attendance.record.impl.WorkDurationServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ClockOutController {

    private final RecordService clockOutServiceImpl;
    private final WorkDurationServiceImpl workDurationServiceImpl;

    /**
     * 退勤処理を行うメソッド
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return リダイレクト先のURL
     */
    @PostMapping("/clock-out")
    public String clockOut(RedirectAttributes attributes) {
        List<AttendanceRecord> todayAttendance = workDurationServiceImpl.getTodayRecordForEmployee();

        if (todayAttendance.isEmpty()) {
            addClockOutErrorAttributes(attributes, "出勤記録がありません。");
            return "redirect:/";
        }

        AttendanceRecord record = todayAttendance.get(0);
        if (record.getClockOut() != null) {
            addClockOutErrorAttributes(attributes, "すでに退勤しています。");
            return "redirect:/";
        }

        clockOutServiceImpl.insert();
        todayAttendance = workDurationServiceImpl.getTodayRecordForEmployee();
        addClockOutSuccessAttributes(attributes, todayAttendance.get(0).getClockOut());

        workDurationServiceImpl.upsertTodayWorkDuration();

        return "redirect:/";
    }
    
    /**
     * 出勤時刻を更新するメソッド
     * @param recordId 出退勤レコードのID
     * @param employeeId 従業員のID
     * @param newClockIn 新しい退勤時刻（hh:mm形式）
     * @param currentClockIn 現在の退勤時刻（yyyy-MM-dd HH:mm:ss形式）
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return リダイレクト先のURL
     */
    @PostMapping("/update-clock-out-time")
    public String updateClockOutTime(
            @RequestParam("recordId") Integer recordId,
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("newClockOut") String newClockOutStr,
            @RequestParam("currentClockOut") String currentClockOutStr,
            @RequestParam(value = "month", required = false) Integer month,
            RedirectAttributes attributes) {

        // 現在の退勤時刻から日付部分を取得
        String datePart = extractDatePart(currentClockOutStr);

        // 日付部分と新しい退勤時間部分を結合してタイムスタンプ形式に変換
        Timestamp newClockOut = createNewTime(datePart, newClockOutStr);

        // 退勤時間の修正を実行
        try {
        	clockOutServiceImpl.update(recordId, employeeId, newClockOut);
            attributes.addFlashAttribute("message", "退勤時刻を修正しました。");
        } catch (Exception e) {
            attributes.addFlashAttribute("message", "退勤時刻の修正に失敗しました。");
            return "redirect:/attendance-year-month";
        }

        attributes.addAttribute("month", month);
        return "redirect:/attendance-year-month";
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
    
    // 日付部分と新しい退勤時間を結合してタイムスタンプに変換するメソッド
    private Timestamp createNewTime(String datePart, String newClockOutStr) {
        String newClockOutFullStr = datePart + " " + newClockOutStr + ":00";
        return Timestamp.valueOf(newClockOutFullStr);
    }
    
    // 現在の退勤時刻から日付部分を抽出するメソッド
    private String extractDatePart(String currentClockOutStr) {
        return currentClockOutStr.split(" ")[0];
    }
    
}
