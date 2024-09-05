package com.levels.ShiftSync.controller.attendance;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.attendance.record.impl.ApprovalServiceImpl;
import com.levels.ShiftSync.service.attendance.record.impl.WorkDurationServiceImpl;
import com.levels.ShiftSync.utility.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final WorkDurationServiceImpl workDurationServiceImpl;
    private final ApprovalServiceImpl approvalServiceImpl;

    /**
     * 今日の勤怠サマリーを表示するメソッド
     * @param model Thymeleafテンプレートにデータを渡すためのモデル
     * @return "summary" テンプレート名
     */
    @GetMapping
    public String showHome(Model model) {
        Integer employeeId = SecurityUtils.getEmployeeIdFromSecurityContext();
        LocalDate today = LocalDate.now();
        List<AttendanceRecord> todayAttendance = workDurationServiceImpl.getTodayRecordForEmployee();

        if (!todayAttendance.isEmpty()) {
            AttendanceRecord record = todayAttendance.get(0);
            model.addAttribute("clockInTime", record.getClockIn());
            model.addAttribute("clockOutTime", record.getClockOut());
        } else {
            model.addAttribute("message", "本日の出勤記録がありません。");
        }

        boolean hasRequest = approvalServiceImpl.hasRequestsForMonth(employeeId, today.getYear() + "-" + today.getMonthValue());
        boolean isNoRequest = approvalServiceImpl.isNoRequest(employeeId, today.getYear() + "-" + today.getMonthValue());
        model.addAttribute("canApproveRequest", isNoRequest && !hasRequest);

        return "attendance";
    }
}
