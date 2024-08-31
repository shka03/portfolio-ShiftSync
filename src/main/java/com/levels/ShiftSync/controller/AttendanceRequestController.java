package com.levels.ShiftSync.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.service.impl.AttendanceRequestServiceImpl;

@Controller
public class AttendanceRequestController {

    @Autowired
    private AttendanceRequestServiceImpl attendanceRequestServiceImpl;

    @GetMapping("/attendance-requests-list")
    public String showRequests(Model model) {
        List<AttendanceRequest> attendanceRequests = attendanceRequestServiceImpl.getAllRequests();
        model.addAttribute("attendance_requests", attendanceRequests);
        return "attendance-requests-list";
    }
    
    @GetMapping("/attendance-approval/{employeeId}/{yearMonth}")
    public String showRequestsByEmployee(
            @PathVariable("employeeId") Integer employeeId,
            @PathVariable("yearMonth") String yearMonth,
            Model model) {
        List<AttendanceRecord> requestRecords = attendanceRequestServiceImpl.getEmployeeMonthRequests(employeeId, yearMonth);
        
        if(requestRecords.isEmpty()) {
        	 model.addAttribute("message", "対象データがありません");
        	return "attendance-approval";
        }
        
        model.addAttribute("attendance_records", requestRecords);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("yearMonth", yearMonth);
        
        String status = attendanceRequestServiceImpl.getApprovalStatus(employeeId, yearMonth);
        model.addAttribute("status", status);
        
        return "attendance-approval";
    }
    
    @PostMapping("/update-approve-status")
    public String updateApproveStatus(
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("yearMonth") String yearMonth,
            @RequestParam("status") String status,
            Model model) {
        attendanceRequestServiceImpl.updateApproveStatus(employeeId, yearMonth, status);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("yearMonth", yearMonth);
        
        return "redirect:/attendance-approval/" + employeeId + "/" + yearMonth;
    }
    
}
