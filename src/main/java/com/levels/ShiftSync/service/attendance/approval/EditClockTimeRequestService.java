package com.levels.ShiftSync.service.attendance.approval;

import java.sql.Timestamp;
import java.util.List;

import com.levels.ShiftSync.entity.AttendanceEditRequest;

public interface EditClockTimeRequestService {

    List<AttendanceEditRequest> getAllRequests();
    
    List<AttendanceEditRequest> getCurrentEditRecord(Integer employeeId, String yearMonthDay);
    
    void updateEditClockTimeRecord(Integer recordId, Integer employeeId, Timestamp newClockInTime, Timestamp newClockOutTime, String newWorkDuration);
    
    void deleteEditClockTimeApproval(Integer employeeId, String yearMonthDay);
    
}
