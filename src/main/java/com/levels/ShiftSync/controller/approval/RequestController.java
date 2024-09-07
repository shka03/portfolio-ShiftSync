package com.levels.ShiftSync.controller.approval;

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
import com.levels.ShiftSync.repository.attendance.record.RecordMapper;
import com.levels.ShiftSync.service.attendance.approval.impl.RequestServiceImpl;

@Controller
public class RequestController {

    @Autowired
    private RequestServiceImpl requestServiceImpl;
    
    @Autowired
    private RecordMapper attendanceRecordMapper;

    @GetMapping("/attendance-requests-list")
    public String showRequests(Model model) {
        List<AttendanceRequest> attendanceRequests = requestServiceImpl.getAllRequests();
        model.addAttribute("attendance_requests", attendanceRequests);
        return "attendance/approval/requests-list";
    }
    
    @GetMapping("/attendance-approval/{employeeId}/{yearMonth}")
    public String showRequestsByEmployee(
            @PathVariable("employeeId") Integer employeeId,
            @PathVariable("yearMonth") String yearMonth,
            Model model) {
        List<AttendanceRecord> requestRecords = attendanceRecordMapper.getRecordForYearByMonth(employeeId, yearMonth);
        
        if(requestRecords.isEmpty()) {
        	 model.addAttribute("message", "対象データがありません");
        	return "attendance/approval/detail";
        }
        
        model.addAttribute("attendance_records", requestRecords);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("yearMonth", yearMonth);
        
        String status = requestServiceImpl.getApprovalStatus(employeeId, yearMonth);
        model.addAttribute("status", status);
        
        return "attendance/approval/detail";
    }
    
    @PostMapping("/update-approve-status")
    public String updateApproveStatus(
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("yearMonth") String yearMonth,
            @RequestParam("status") String status,
            Model model) {
        requestServiceImpl.updateApproveStatus(employeeId, yearMonth, status);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("yearMonth", yearMonth);
        
        return "redirect:/attendance-approval/" + employeeId + "/" + yearMonth;
    }
    
    @PostMapping("/delete-request")
    public String deleteRequest(
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("yearMonth") String yearMonth,
            Model model) {
        requestServiceImpl.deleteRequest(employeeId, yearMonth);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("yearMonth", yearMonth);
        
        return "redirect:/attendance-approval/" + employeeId + "/" + yearMonth;
    }
    
}
