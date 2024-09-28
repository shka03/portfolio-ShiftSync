package com.levels.ShiftSync.controller.attendance;

import java.sql.Timestamp;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.attendance.record.impl.EditClockTimeServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@Transactional
public class EditClockTimeController {
	
	private final EditClockTimeServiceImpl editClockTimeServiceImpl;

	@PostMapping("/edit-clock-time")
	public String editClockTime(@RequestParam("recordId") Integer recordId,
	                            @RequestParam("employeeId") Integer employeeId,
	                            @RequestParam("clockIn") String clockIn,
	                            @RequestParam("clockOut") String clockOut,
	                            @RequestParam("workDuration") String workDuration,
	                            @RequestParam("selectedMonth") Integer selectedMonth,
	                            Model model) {
	    
	    AttendanceRecord attendanceRecord = new AttendanceRecord();
	    attendanceRecord.setRecordId(recordId);
	    attendanceRecord.setEmployeeId(employeeId);
	    attendanceRecord.setClockIn(Timestamp.valueOf(clockIn));
	    attendanceRecord.setClockOut(Timestamp.valueOf(clockOut));
	    attendanceRecord.setWorkDuration(workDuration);

	    model.addAttribute("attendance_record", attendanceRecord);
	    model.addAttribute("selectedMonth", selectedMonth);
	    
	    String yearMonthDay = clockIn.substring(0, 10);
	    boolean canEditTimeRequest = editClockTimeServiceImpl.getCurrentEditRecord(employeeId, yearMonthDay).isEmpty();
	    System.out.println("canEditTimeRequestの結果" + canEditTimeRequest);
	    model.addAttribute("canEditTimeRequest", canEditTimeRequest);
		
	    return "attendance/record/edit-clock-time";
	}
	
    @PostMapping("/update-clock-times")
    public String updateClockTimes(
            @RequestParam("recordId") Integer recordId,
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("newClockIn") String newClockInStr,
            @RequestParam("newClockOut") String newClockOutStr,
            @RequestParam("currentClockIn") String currentClockInStr,
            @RequestParam("applicationReason") String applicationReason,
            RedirectAttributes attributes,
            Model model) {

        // 出勤時刻と退勤時刻の更新
    	String datePart = extractDatePartFromCurrentClockIn(currentClockInStr, attributes);
    	Timestamp newClockIn = createNewTime(datePart, newClockInStr, attributes);
        Timestamp newClockOut = createNewTime(datePart, newClockOutStr, attributes);

        try {
        	editClockTimeServiceImpl.updateClockInAndOut(
        			recordId,
        			employeeId,
        			datePart, 
        			newClockIn,
        			newClockOut, 
        			applicationReason);
            attributes.addFlashAttribute("message", "出勤・退勤時刻を修正しました。");
        } catch (Exception e) {
            attributes.addFlashAttribute("message", "出勤・退勤時刻の修正に失敗しました。");
        }

        // 修正後の勤怠データを再取得
        AttendanceRecord attendanceRecord = editClockTimeServiceImpl.getCurrentRecord(recordId);
        model.addAttribute("attendance_record", attendanceRecord);

        return "attendance/record/edit-clock-time";
    }
    
    /**
     * 現在の出勤時刻から日付部分を抽出します。
     * @param currentClockInStr 現在の出勤時刻（yyyy-MM-dd HH:mm:ss形式）
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return 日付部分（yyyy-MM-dd形式）または、形式が不正な場合はnull
     */
    private String extractDatePartFromCurrentClockIn(String currentClockInStr, RedirectAttributes attributes) {
        return currentClockInStr.split(" ")[0];
    }
        
    /**
     * 新しい出勤時刻をTimestampに変換します。
     * @param datePart 現在の出勤時刻から抽出した日付部分（yyyy-MM-dd形式）
     * @param newClockInStr 新しい出勤時刻（hh:mm形式）
     * @param attributes リダイレクト時にFlashAttributesにデータを追加
     * @return 新しい出勤時刻のTimestamp型で返す
     */
    private Timestamp createNewTime(String datePart, String newClockStr, RedirectAttributes attributes) {
        String newClockFullStr = String.format("%s %s:00", datePart, newClockStr);
        return Timestamp.valueOf(newClockFullStr);
    }

}
