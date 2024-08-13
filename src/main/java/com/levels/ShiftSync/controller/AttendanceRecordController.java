package com.levels.ShiftSync.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.levels.ShiftSync.entity.LoginUser;
import com.levels.ShiftSync.service.impl.AttendanceRecordServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AttendanceRecordController {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceRecordController.class);
    
    private final AttendanceRecordServiceImpl attendanceRecordServiceImpl;

    // 勤怠記録画面を表示する
    @GetMapping
    public String showAttendancePag(Model model) {
        Integer employeeId = getEmployeeIdFromSecurityContext();
        
        // ログイン後に出勤状況をデータベースからチェック
        boolean isClockedInToday = attendanceRecordServiceImpl.isClockedInToday(employeeId);
        model.addAttribute("isClockedInToday", isClockedInToday);

        return "attendance"; // ボタンの状態を反映したページを返す
    }
    
    @PostMapping("/clock-in")
    public String clockIn(Model model) {
        return checkClockIn(model);
    }
    
    @PostMapping("/clock-out")
    public String clockOutTime() {
        attendanceRecordServiceImpl.clockOutTime();
        return "redirect:/";
    }
    
    // 出勤状態を確認した結果を反映したページを変えす。
    private String checkClockIn(Model model) {
        Integer employeeId = getEmployeeIdFromSecurityContext();
        
        // 出勤済みかどうかをデータベースからチェック
        if (attendanceRecordServiceImpl.isClockedInToday(employeeId)) {
            model.addAttribute("isClockedInToday", true);
            return "attendance";
        }
        
        // 出勤時間の登録
        attendanceRecordServiceImpl.clockInTime();
        
        // 再度、フラグを更新
        model.addAttribute("isClockedInToday", true);
        
        // リダイレクトではなく、状態を反映したページを返す
        return "attendance";
    }
    
    private Integer getEmployeeIdFromSecurityContext() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser.getEmployeeId();
    }
}
