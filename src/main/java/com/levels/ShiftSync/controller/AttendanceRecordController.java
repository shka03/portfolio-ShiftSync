package com.levels.ShiftSync.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String showAttendancePage() {
    	return "attendance";
    }
    
    @PostMapping("/clock-in")
    public String clockIn() {
        try {
//            // 今日出勤できるかどうかをチェック
//            if (!attendanceService.canClockInToday(employeeId)) {
//                // 出勤できない場合、エラーページにリダイレクト
//                return "error/403";
//            }

        	attendanceRecordServiceImpl.clockInTime();
            return "redirect:/";
        } catch (Exception e) {
            logger.error("Error while clocking in for employee ID: " + e.getMessage());
            return "error/500";
        }
    }
    
    @PostMapping("/clock-out")
    public String clockOutTime() {
        try {
        	attendanceRecordServiceImpl.clockOutTime();
            return "redirect:/";
        } catch (Exception e) {
            logger.error("Error while clocking in for employee ID: " + e.getMessage());
            return "error/500";
        }
    }
}
