package com.levels.ShiftSync.repository.attendance.approval;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.levels.ShiftSync.entity.AttendanceEditRequest;

@Mapper
public interface EditClockTimeRequestMapper {

    List<AttendanceEditRequest> getAllRequests();
    
    List<AttendanceEditRequest> getCurrentEditRecord(Integer employeeId, String yearMonthDay);
    
    void updateEditClockTimeRecord(Integer recordId, Integer employeeId, Timestamp newClockInTime, Timestamp newClockOutTime, String newWorkDuration);
    
    void deleteEditClockTimeApproval(Integer employeeId, String yearMonthDay);
    
}
