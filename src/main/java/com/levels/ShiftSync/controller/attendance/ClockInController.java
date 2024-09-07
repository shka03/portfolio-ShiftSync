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
public class ClockInController {

    private final RecordService clockInServiceImpl;
    private final WorkDurationServiceImpl workDurationServiceImpl;

    /**
     * 出勤処理を行うメソッド
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return リダイレクト先のURL
     */
    @PostMapping("/clock-in")
    public String clockIn(RedirectAttributes attributes) {
        List<AttendanceRecord> todayAttendance = workDurationServiceImpl.getTodayRecordForEmployee();

        if (!todayAttendance.isEmpty()) {
            handleAlreadyClockedIn(attributes, todayAttendance.get(0).getClockIn());
            return "redirect:/";
        }

        clockInServiceImpl.insert();
        todayAttendance = workDurationServiceImpl.getTodayRecordForEmployee();
        addClockInSuccessAttributes(attributes, todayAttendance.get(0).getClockIn());
        return "redirect:/";
    }
    /**
     * 出勤時刻を更新するメソッド
     * @param recordId 出退勤レコードのID
     * @param employeeId 従業員のID
     * @param newClockIn 新しい出勤時刻（hh:mm形式）
     * @param currentClockIn 現在の出勤時刻（yyyy-MM-dd HH:mm:ss形式）
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return リダイレクト先のURL
     */
    @PostMapping("/update-clock-in-time")
    public String updateClockInTime(
            @RequestParam("recordId") Integer recordId,
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("newClockIn") String newClockInStr,
            @RequestParam("currentClockIn") String currentClockInStr,
            @RequestParam(value = "month", required = false) Integer month,
            RedirectAttributes attributes) {

        // 入力パラメータの検証
        if (isInvalidClockInParameter(newClockInStr, currentClockInStr)) {
            attributes.addFlashAttribute("message", "出勤時刻が無効です。");
            return "redirect:/attendance-year-month";
        }

        // 現在の出勤時刻から日付部分を取得
        String datePart = extractDatePartFromCurrentClockIn(currentClockInStr, attributes);
        if (datePart == null) {
            return "redirect:/attendance-year-month";
        }

        // 新しい出勤時刻のパースと検証
        Timestamp newClockIn = parseNewClockIn(datePart, newClockInStr, attributes);
        if (newClockIn == null) {
            return "redirect:/attendance-year-month";
        }

        try {
            // 出勤時刻の更新処理
        	clockInServiceImpl.update(recordId, employeeId, newClockIn);
            attributes.addFlashAttribute("message", "出勤時刻を修正しました。");
        } catch (Exception e) {
            // 更新処理中の例外処理
            attributes.addFlashAttribute("message", "出勤時刻の修正に失敗しました。");
        }
        
        attributes.addAttribute("month", month); // 月のパラメータを追加
        return "redirect:/attendance-year-month";
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
    	attributes.addFlashAttribute("clockInErrorMessage", "すでに出勤しています。");
    }
    
    /**
     * 現在の出勤時刻から日付部分を抽出します。
     * @param currentClockInStr 現在の出勤時刻（yyyy-MM-dd HH:mm:ss形式）
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return 日付部分（yyyy-MM-dd形式）または、形式が不正な場合はnull
     */
    private String extractDatePartFromCurrentClockIn(String currentClockInStr, RedirectAttributes attributes) {
        if (currentClockInStr == null || !currentClockInStr.contains(" ")) {
            attributes.addFlashAttribute("message", "現在の出勤時刻の形式が不正です。");
            return null;
        }

        try {
            return currentClockInStr.split(" ")[0];
        } catch (Exception e) {
            attributes.addFlashAttribute("message", "現在の出勤時刻の形式が不正です。");
            return null;
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
     * 出勤時刻と現在の出勤時刻の文字列が有効かどうかをチェックします。
     * @param newClockInStr 新しい出勤時刻（hh:mm形式）
     * @param currentClockInStr 現在の出勤時刻（yyyy-MM-dd HH:mm:ss形式）
     * @return 出勤時刻が無効な場合はtrue、それ以外はfalse
     */
    private boolean isInvalidClockInParameter(String newClockInStr, String currentClockInStr) {
        return newClockInStr == null || newClockInStr.isEmpty() ||
               currentClockInStr == null || currentClockInStr.isEmpty();
    }
    
    /**
     * 新しい出勤時刻をパースしてTimestampに変換します。
     * @param datePart 現在の出勤時刻から抽出した日付部分（yyyy-MM-dd形式）
     * @param newClockInStr 新しい出勤時刻（hh:mm形式）
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return 新しい出勤時刻のTimestampまたは、形式が不正な場合はnull
     */
    private Timestamp parseNewClockIn(String datePart, String newClockInStr, RedirectAttributes attributes) {
        if (newClockInStr == null || newClockInStr.isEmpty()) {
            attributes.addFlashAttribute("message", "新しい出勤時刻の形式が不正です。");
            return null;
        }

        String newClockInFullStr = String.format("%s %s:00", datePart, newClockInStr);
        try {
            return Timestamp.valueOf(newClockInFullStr);
        } catch (IllegalArgumentException e) {
            attributes.addFlashAttribute("message", "新しい出勤時刻の形式が不正です。");
            return null;
        }
    }
}
