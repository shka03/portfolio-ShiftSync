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
        attendanceRecordServiceImpl.clockInTime();
        return "redirect:/";
    }
    
    @PostMapping("/clock-out")
    public String clockOutTime() {
        attendanceRecordServiceImpl.clockOutTime();
        return "redirect:/";
    }
}
