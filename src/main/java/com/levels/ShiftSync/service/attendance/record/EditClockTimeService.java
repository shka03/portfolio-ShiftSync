package com.levels.ShiftSync.service.attendance.record;

import java.sql.Timestamp;

import com.levels.ShiftSync.entity.AttendanceRecord;

public interface EditClockTimeService {

    public void updateClockInAndOut(
    		Integer recordId,
    		Integer employeeId,
    		String yearMonth,
    		Timestamp newClockIn,
    		Timestamp newClockOut,
    		String applicationReason);

    AttendanceRecord getCurrentRecord(Integer recordId);
}
