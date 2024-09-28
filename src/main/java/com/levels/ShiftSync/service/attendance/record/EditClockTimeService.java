package com.levels.ShiftSync.service.attendance.record;

import java.sql.Timestamp;

import com.levels.ShiftSync.entity.AttendanceEditRequest;
import com.levels.ShiftSync.entity.AttendanceRecord;

public interface EditClockTimeService {

    public void editRequestClockInAndOut(
    		Integer recordId,
    		Integer employeeId,
    		String yearMonth,
    		Timestamp newClockIn,
    		Timestamp newClockOut,
    		String applicationReason);

    AttendanceRecord getCurrentRecord(Integer recordId);
    
    AttendanceEditRequest getCurrentEditRecord(Integer employeeId, String yearMonthDay);
}
