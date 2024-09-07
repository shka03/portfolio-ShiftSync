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
        	addClockInErrorAttributes(attributes, todayAttendance.get(0).getClockIn());
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

        // 現在の出勤時刻から日付部分を取得
        String datePart = extractDatePartFromCurrentClockIn(currentClockInStr, attributes);

        // 新しい出勤時刻
        Timestamp newClockIn = createNewTime(datePart, newClockInStr, attributes);
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
        return currentClockInStr.split(" ")[0];
    }
        
    /**
     * 新しい出勤時刻をTimestampに変換します。
     * @param datePart 現在の出勤時刻から抽出した日付部分（yyyy-MM-dd形式）
     * @param newClockInStr 新しい出勤時刻（hh:mm形式）
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return 新しい出勤時刻のTimestamp型で返す
     */
    private Timestamp createNewTime(String datePart, String newClockInStr, RedirectAttributes attributes) {
        String newClockInFullStr = String.format("%s %s:00", datePart, newClockInStr);
        return Timestamp.valueOf(newClockInFullStr);
    }
}
