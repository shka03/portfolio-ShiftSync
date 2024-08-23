package com.levels.ShiftSync.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.impl.AttendanceRecordServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AttendanceRecordController {

    private final AttendanceRecordServiceImpl attendanceRecordService;

    /**
     * 今日の勤怠情報を表示するメソッド
     * @param model Thymeleafテンプレートにデータを渡すためのモデル
     * @return "attendance" テンプレート名
     */
    @GetMapping
    public String showAttendancePage(Model model) {
        List<AttendanceRecord> todayAttendance = attendanceRecordService.getTodayAttendance();
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
        List<AttendanceRecord> todayAttendance = attendanceRecordService.getTodayAttendance();

        // 既に出勤済みの場合の処理
        if (!todayAttendance.isEmpty()) {
            handleAlreadyClockedIn(attributes, todayAttendance.get(0).getClockIn());
            return "redirect:/";
        }

        // 出勤処理を実行
        attendanceRecordService.clockInTime();
        todayAttendance = attendanceRecordService.getTodayAttendance();
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
    @PostMapping("/updateClockInTime")
    public String updateClockInTime(
            @RequestParam("recordId") Integer recordId,
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("newClockIn") String newClockInStr,
            @RequestParam("currentClockIn") String currentClockInStr,
            RedirectAttributes attributes) {

        // 入力パラメータの検証
        if (isInvalidClockInParameter(newClockInStr, currentClockInStr)) {
            attributes.addFlashAttribute("message", "出勤時刻が無効です。");
            return "redirect:/yearly_attendance";
        }

        // 現在の出勤時刻から日付部分を取得
        String datePart = extractDatePartFromCurrentClockIn(currentClockInStr, attributes);
        if (datePart == null) {
            return "redirect:/yearly_attendance";
        }

        // 新しい出勤時刻のパースと検証
        Timestamp newClockIn = parseNewClockIn(datePart, newClockInStr, attributes);
        if (newClockIn == null) {
            return "redirect:/yearly_attendance";
        }

        // 出勤時間の更新と結果の設定
        return updateClockInTime(recordId, employeeId, newClockIn, attributes);
    }

    private boolean isInvalidClockInParameter(String newClockInStr, String currentClockInStr) {
        return newClockInStr == null || newClockInStr.isEmpty() ||
               currentClockInStr == null || currentClockInStr.isEmpty();
    }

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


    private String updateClockInTime(Integer recordId, Integer employeeId, Timestamp newClockIn, RedirectAttributes attributes) {
        try {
            attendanceRecordService.updateClockInTime(recordId, employeeId, newClockIn);
            attributes.addFlashAttribute("message", "出勤時刻を修正しました。");
        } catch (Exception e) {
            attributes.addFlashAttribute("message", "出勤時刻の修正に失敗しました。");
        }
        return "redirect:/yearly_attendance";
    }

    /**
     * 退勤処理を行うメソッド
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return リダイレクト先のURL
     */
    @PostMapping("/clock-out")
    public String clockOut(RedirectAttributes attributes) {
        List<AttendanceRecord> todayAttendance = attendanceRecordService.getTodayAttendance();

        // 出勤記録がない場合の処理
        if (todayAttendance.isEmpty()) {
            addClockOutErrorAttributes(attributes, "出勤記録がありません。");
            return "redirect:/";
        }

        AttendanceRecord record = todayAttendance.get(0);
        // 既に退勤済みの場合の処理
        if (record.getClockOut() != null) {
            addClockOutErrorAttributes(attributes, "すでに退勤しています。");
            return "redirect:/";
        }

        // 退勤処理を実行
        attendanceRecordService.clockOutTime();
        todayAttendance = attendanceRecordService.getTodayAttendance();
        addClockOutSuccessAttributes(attributes, todayAttendance.get(0).getClockOut());
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
    @PostMapping("/updateClockOutTime")
    public String updateClockOutTime(
            @RequestParam("recordId") Integer recordId,
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("newClockOut") String newClockOutStr,
            @RequestParam("currentClockOut") String currentClockOutStr,
            RedirectAttributes attributes) {

        // 入力パラメータの検証
        if (isInvalidTimeParameter(newClockOutStr, currentClockOutStr)) {
            attributes.addFlashAttribute("message", "退勤時刻が無効です。");
            return "redirect:/yearly_attendance";
        }

        // 現在の退勤時刻から日付部分を取得
        String datePart;
        try {
            datePart = extractDatePart(currentClockOutStr);
        } catch (ArrayIndexOutOfBoundsException e) {
            attributes.addFlashAttribute("message", "現在の退勤時刻の形式が不正です。");
            return "redirect:/yearly_attendance";
        }

        // 日付部分と新しい退勤時間部分を結合してタイムスタンプ形式に変換
        Timestamp newClockOut;
        try {
            newClockOut = createTimestamp(datePart, newClockOutStr);
        } catch (IllegalArgumentException e) {
            attributes.addFlashAttribute("message", "新しい退勤時刻の形式が不正です。");
            return "redirect:/yearly_attendance";
        }

        // 退勤時間の修正を実行
        try {
            attendanceRecordService.updateClockOutTime(recordId, employeeId, newClockOut);
            attributes.addFlashAttribute("message", "退勤時刻を修正しました。");
        } catch (Exception e) {
            attributes.addFlashAttribute("message", "退勤時刻の修正に失敗しました。");
        }

        return "redirect:/yearly_attendance";
    }

    // 入力パラメータが無効かどうかを確認するメソッド
    private boolean isInvalidTimeParameter(String newClockOutStr, String currentClockOutStr) {
        return newClockOutStr == null || newClockOutStr.isEmpty() ||
               currentClockOutStr == null || currentClockOutStr.isEmpty();
    }

    // 現在の退勤時刻から日付部分を抽出するメソッド
    private String extractDatePart(String currentClockOutStr) {
        if (currentClockOutStr == null || !currentClockOutStr.contains(" ")) {
            throw new ArrayIndexOutOfBoundsException("Invalid format for currentClockOut.");
        }
        return currentClockOutStr.split(" ")[0];
    }

    // 日付部分と新しい退勤時間を結合してタイムスタンプに変換するメソッド
    private Timestamp createTimestamp(String datePart, String newClockOutStr) {
        String newClockOutFullStr = datePart + " " + newClockOutStr + ":00";
        try {
            return Timestamp.valueOf(newClockOutFullStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid format for newClockOut.");
        }
    }

    /**
     * 任意の月の勤怠履歴を表示するメソッド
     * @param month 表示する月 (1月 = 1, 12月 = 12)
     * @param model Thymeleafテンプレートにデータを渡すためのモデル
     * @return "yearly_attendance" テンプレート名
     */
    @GetMapping("/yearly_attendance")
    public String showYearlyAttendance(
            @RequestParam(value = "month", required = false) Integer month, 
            Model model
    ) {
        // month が null なら、現在の月を使用する
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }
        
        List<AttendanceRecord> yearlyAttendance = attendanceRecordService.getYearlyAttendanceForMonth(month);

        // データが存在しない場合は、エラーメッセージをモデルに追加し、早期にリターン
        if (yearlyAttendance.isEmpty()) {
            model.addAttribute("message", "選択された月のデータはありません。");
            model.addAttribute("selectedMonth", month); // 選択された月もモデルに追加
            return "yearly_attendance";
        }

        // データが存在する場合、勤怠記録をモデルに追加
        model.addAttribute("attendance_records", yearlyAttendance);

        // 選択された月をモデルに追加
        model.addAttribute("selectedMonth", month);

        return "yearly_attendance";
    }
    
    /**
     * 今日の勤怠情報をModelに設定するメソッド
     * @param model Modelにデータを追加するためのオブジェクト
     * @param todayAttendance 今日の勤怠記録のリスト
     */
    private void populateAttendanceModel(Model model, List<AttendanceRecord> todayAttendance) {
        if (todayAttendance.isEmpty()) {
            model.addAttribute("clockInTime", null);
            model.addAttribute("clockOutTime", null);
            return;
        }

        AttendanceRecord record = todayAttendance.get(0);
        model.addAttribute("clockInTime", record.getClockIn());
        model.addAttribute("clockOutTime", record.getClockOut());
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
        attributes.addFlashAttribute("clockInErrorMessage", "すでに出勤しています。");
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
}
