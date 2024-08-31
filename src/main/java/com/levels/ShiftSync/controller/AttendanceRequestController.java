package com.levels.ShiftSync.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.service.impl.AttendanceRequestServiceImpl;

@Controller
public class AttendanceRequestController {

    @Autowired
    private AttendanceRequestServiceImpl attendanceRequestServiceImpl;

    @GetMapping("/attendance-requests-list")
    public String showAttendanceRequests(Model model) {
        List<AttendanceRequest> attendanceRequests = attendanceRequestServiceImpl.getAllAttendanceRequests();
        model.addAttribute("attendance_requests", attendanceRequests);
        return "attendance-requests-list";
    }
}
