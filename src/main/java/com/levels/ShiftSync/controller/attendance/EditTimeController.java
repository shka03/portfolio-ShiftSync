package com.levels.ShiftSync.controller.attendance;

import java.sql.Timestamp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.levels.ShiftSync.entity.AttendanceRecord;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EditTimeController {

	@PostMapping("/edit-clock-time")
	public String editClockTime(@RequestParam("recordId") Integer recordId,
	                            @RequestParam("employeeId") Integer employeeId,
	                            @RequestParam("clockIn") String clockIn,
	                            @RequestParam("clockOut") String clockOut,
	                            @RequestParam("workDuration") String workDuration,
	                            @RequestParam("selectedMonth") Integer selectedMonth,
	                            Model model) {
	    System.out.println("recordId: " + recordId);
	    System.out.println("employeeId: " + employeeId);
	    System.out.println("clockIn: " + clockIn);
	    System.out.println("clockOut: " + clockOut);
	    System.out.println("selectedMonth: " + selectedMonth);
	    
	    AttendanceRecord attendanceRecord = new AttendanceRecord();
	    attendanceRecord.setRecordId(recordId);
	    attendanceRecord.setEmployeeId(employeeId);
	    attendanceRecord.setClockIn(Timestamp.valueOf(clockIn));
	    attendanceRecord.setClockOut(Timestamp.valueOf(clockOut));
	    attendanceRecord.setWorkDuration(workDuration);

	    model.addAttribute("attendance_record", attendanceRecord);
	    model.addAttribute("selectedMonth", selectedMonth);
		
	    return "attendance/record/edit-clock-time";
	}

}
