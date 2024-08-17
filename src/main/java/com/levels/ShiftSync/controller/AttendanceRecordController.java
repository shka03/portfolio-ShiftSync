package com.levels.ShiftSync.controller;

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

    @GetMapping
    public String showAttendancePage(Model model) {
    	List<AttendanceRecord> todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();
        
        if (!todayAttendance.isEmpty()) {
            model.addAttribute("clockInTime", todayAttendance.get(0).getClockIn());
        } else {
            model.addAttribute("clockInTime", null); // 適切なデフォルト値
        }
        
    	return "attendance";
    }
    
    // 勤怠登録画面に戻ることからPRGパターンで実装
    @PostMapping("/clock-in")
    public String clockIn(RedirectAttributes attributes) {
    	List<AttendanceRecord> todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();
    	
    	if (todayAttendance == null || todayAttendance.isEmpty()) {
    		attendanceRecordServiceImpl.clockInTime();
    		todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();
    		attributes.addFlashAttribute("clockInTime", todayAttendance.get(0).getClockIn());
    		attributes.addFlashAttribute("clockInSuccessMessage", "おはようございます。出勤しました。");
    		return "redirect:/";
    	} else {
    		todayAttendance = attendanceRecordServiceImpl.getTodayAttendance();
    		attributes.addFlashAttribute("clockInTime", todayAttendance.get(0).getClockIn());
    		attributes.addFlashAttribute("clockErrorMessage", "すでに出勤してます。");
    		return "redirect:/";
    	}
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
