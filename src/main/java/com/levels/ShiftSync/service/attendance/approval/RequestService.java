package com.levels.ShiftSync.service.attendance.approval;

import java.util.List;

import com.levels.ShiftSync.entity.AttendanceRequest;

public interface RequestService {

    List<AttendanceRequest> getAllRequests();
    
    String getApprovalStatus(Integer employeeId, String yearMonth);
    
    void updateApproveStatus(Integer employeeId, String yearMonth, String status);

    void deleteRequest(Integer employeeId, String yearMonth);

}
