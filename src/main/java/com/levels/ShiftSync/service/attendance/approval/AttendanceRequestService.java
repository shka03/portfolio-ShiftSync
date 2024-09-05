package com.levels.ShiftSync.service.attendance.approval;

import java.util.List;

import com.levels.ShiftSync.entity.AttendanceRequest;

public interface AttendanceRequestService {

    /**
     * @return 勤怠承認申請リスト
     */
    List<AttendanceRequest> getAllRequests();
    
    String getApprovalStatus(Integer employeeId, String yearMonth);
    
    /**
     * @param employeeId 従業員ID
     * @param yearMonth 年月
     * @param status 承認状態
     */
    void updateApproveStatus(Integer employeeId, String yearMonth, String status);
    
    void deleteRequest(Integer employeeId, String yearMonth);

}
