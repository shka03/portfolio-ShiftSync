package com.levels.ShiftSync.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.impl.AttendanceRecordServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AttendanceRecordController {   
    private final AttendanceRecordServiceImpl attendanceRecordServiceImpl;

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

    @GetMapping("/monthly_attendance")
    public String showMonthlyAttendancePage(Model model) {
        List<AttendanceRecord> monthlyAttendance = attendanceRecordServiceImpl.getMonthlyAttendance();
        if (monthlyAttendance == null || monthlyAttendance.isEmpty()) {
            model.addAttribute("message", "今月の勤怠データはまだありません。");
        } else {
            model.addAttribute("attendance_records", monthlyAttendance);
        }
        return "monthly_attendance";
    }

}
