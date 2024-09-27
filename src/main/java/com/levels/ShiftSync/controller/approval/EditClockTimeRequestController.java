package com.levels.ShiftSync.controller.approval;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.levels.ShiftSync.entity.AttendanceEditRequest;
import com.levels.ShiftSync.service.attendance.approval.impl.EditClockTimeRequestServiceImpl;

@Controller
public class EditClockTimeRequestController {
    
	@Autowired
	private EditClockTimeRequestServiceImpl editClockTimeRequestServiceImpl;
	
	@GetMapping("/edit-clock-time-request-list")
    public String showRequests(Model model) {
        List<AttendanceEditRequest> attendanceEditRequests = editClockTimeRequestServiceImpl.getAllRequests();
        model.addAttribute("attendance_time_corrections", attendanceEditRequests);
        return "attendance/approval/edit-clock-time-request-list";
    }
	
    @GetMapping("/edit-clock-time-request-list/{employeeId}/{yearMonthDay}")
    public String showRequestsByEmployee(
            @PathVariable("employeeId") Integer employeeId,
            @PathVariable("yearMonthDay") String yearMonthDay,
            Model model) {
    	List<AttendanceEditRequest> requestRecord = editClockTimeRequestServiceImpl.getCurrentEditRecord(employeeId, yearMonthDay);
        
        if(requestRecord.isEmpty()) {
        	 model.addAttribute("message", "対象データがありません");
        	return "attendance/approval/edit-clock-time-request-detail";
        }
        
        model.addAttribute("attendance_time_corrections", requestRecord);
        model.addAttribute("recordId", employeeId);
        model.addAttribute("yearMonthDay", yearMonthDay);
        
        return "attendance/approval/edit-clock-time-request-detail";
    }
    
    @PostMapping("/update-edit-clock-time-request")
    public String updateApproveStatus(
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("yearMonthDay") String yearMonthDay,
            RedirectAttributes redirectAttributes) {
    	List<AttendanceEditRequest> requestRecord = editClockTimeRequestServiceImpl.getCurrentEditRecord(employeeId, yearMonthDay);
    	Integer updateRecordId = requestRecord.getFirst().getRecordId();
    	Timestamp newClockInTime = requestRecord.getFirst().getClockIn();
    	Timestamp newClockOutTime = requestRecord.getFirst().getClockOut();
    	String newWorkDuration = requestRecord.getFirst().getWorkDuration();
    	
    	editClockTimeRequestServiceImpl.updateEditClockTimeRecord(updateRecordId, employeeId, newClockInTime, newClockOutTime, newWorkDuration);
    	editClockTimeRequestServiceImpl.deleteEditClockTimeApproval(employeeId, yearMonthDay);
        redirectAttributes.addFlashAttribute("approveMessage", "承認しました");
        
        return "redirect:/edit-clock-time-request-list";
    }
    
    @PostMapping("/delete-edit-clock-time-request")
    public String deleteRequest(
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("yearMonthDay") String yearMonthDay,
            RedirectAttributes redirectAttributes) {
    	editClockTimeRequestServiceImpl.deleteEditClockTimeApproval(employeeId, yearMonthDay);
        redirectAttributes.addFlashAttribute("approveMessage", "申請を取り下げました。もう一度、申請してください。");
        
        return "redirect:/edit-clock-time-request-list";
    }
	
}
