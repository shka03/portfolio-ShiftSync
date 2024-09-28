package com.levels.ShiftSync.controller.attendance;

import java.sql.Timestamp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.levels.ShiftSync.entity.AttendanceEditRequest;
import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.attendance.record.impl.EditClockTimeServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EditClockTimeController {

    private final EditClockTimeServiceImpl editClockTimeServiceImpl;

    @PostMapping("/edit-clock-time")
    public String editClockTime(
            @RequestParam("recordId") Integer recordId,
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("clockIn") String clockIn,
            @RequestParam("clockOut") String clockOut,
            @RequestParam("workDuration") String workDuration,
            @RequestParam("selectedMonth") Integer selectedMonth,
            Model model) {

        AttendanceRecord attendanceRecord = createAttendanceRecord(recordId, employeeId, clockIn, clockOut, workDuration, model);
        model.addAttribute("attendance_record", attendanceRecord);
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("canEditTimeRequest", canEditTimeRequest(employeeId, clockIn));

        return "attendance/record/edit-clock-time";
    }

    @PostMapping("/edit-clock-time-request")
    public String updateClockTimes(
            @RequestParam("recordId") Integer recordId,
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("newClockIn") String newClockInStr,
            @RequestParam("newClockOut") String newClockOutStr,
            @RequestParam("currentClockIn") String currentClockInStr,
            @RequestParam("applicationReason") String applicationReason,
            Model model) {

        String datePart = extractDatePartFromCurrentClockIn(currentClockInStr);
        Timestamp newClockIn = createNewTime(datePart, newClockInStr);
        Timestamp newClockOut = createNewTime(datePart, newClockOutStr);

        try {
            editClockTimeServiceImpl.editRequestClockInAndOut(recordId, employeeId, datePart, newClockIn, newClockOut, applicationReason);
            model.addAttribute("message", "出勤・退勤時刻を修正申請をしました。");
        } catch (Exception e) {
            model.addAttribute("message", "出勤・退勤時刻の修正申請に失敗しました。もう一度、ご対応をお願いいたします。");
            return "attendance/record/edit-clock-time";
        }

        AttendanceRecord attendanceRecord = createAttendanceRecordFromEditRequest(employeeId, datePart);
        model.addAttribute("attendance_record", attendanceRecord);
        model.addAttribute("selectedMonth", datePart.substring(5, 7)); // 月は0から始まるため

        return "attendance/record/edit-clock-time";
    }

    private AttendanceRecord createAttendanceRecord(Integer recordId, Integer employeeId, String clockIn, String clockOut, String workDuration, Model model) {
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        String datePart = extractDatePartFromCurrentClockIn(clockIn);

        AttendanceEditRequest existingRequest = editClockTimeServiceImpl.getCurrentEditRecord(employeeId, datePart);
        if (existingRequest != null) {
            attendanceRecord.setRecordId(recordId);
            attendanceRecord.setEmployeeId(employeeId);
            attendanceRecord.setClockIn(existingRequest.getClockIn());
            attendanceRecord.setClockOut(existingRequest.getClockOut());
            attendanceRecord.setWorkDuration(existingRequest.getWorkDuration());
            model.addAttribute("message", "申請済です。修正したい場合は管理者に取り下げをご依頼ください");
        } else {
            attendanceRecord.setRecordId(recordId);
            attendanceRecord.setEmployeeId(employeeId);
            attendanceRecord.setClockIn(Timestamp.valueOf(clockIn));
            attendanceRecord.setClockOut(Timestamp.valueOf(clockOut));
            attendanceRecord.setWorkDuration(workDuration);
        }
        return attendanceRecord;
    }

    private boolean canEditTimeRequest(Integer employeeId, String clockIn) {
        String yearMonthDay = clockIn.substring(0, 10);
        return editClockTimeServiceImpl.getCurrentEditRecord(employeeId, yearMonthDay) == null;
    }

    private AttendanceRecord createAttendanceRecordFromEditRequest(Integer employeeId, String datePart) {
        AttendanceEditRequest attendanceEditRequest = editClockTimeServiceImpl.getCurrentEditRecord(employeeId, datePart);
        AttendanceRecord attendanceRecord = new AttendanceRecord();

        if (attendanceEditRequest != null) {
            attendanceRecord.setRecordId(attendanceEditRequest.getRecordId());
            attendanceRecord.setEmployeeId(attendanceEditRequest.getEmployeeId());
            attendanceRecord.setClockIn(attendanceEditRequest.getClockIn());
            attendanceRecord.setClockOut(attendanceEditRequest.getClockOut());
            attendanceRecord.setWorkDuration(attendanceEditRequest.getWorkDuration());
        }

        return attendanceRecord;
    }

    private Timestamp createNewTime(String datePart, String newClockStr) {
        String newClockFullStr = String.format("%s %s:00", datePart, newClockStr);
        return Timestamp.valueOf(newClockFullStr);
    }

    private String extractDatePartFromCurrentClockIn(String currentClockInStr) {
        return currentClockInStr.split(" ")[0];
    }
}

