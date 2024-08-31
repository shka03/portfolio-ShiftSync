package com.levels.ShiftSync.service;

import java.util.List;

import com.levels.ShiftSync.entity.AttendanceRequest;

public interface AttendanceRequestService {

    /**
     * 全ての勤怠承認申請を取得するメソッド
     * @return 勤怠承認申請リスト
     */
    public List<AttendanceRequest> getAllAttendanceRequests();

    /**
     * 従業員IDに基づいて勤怠承認申請を取得するメソッド
     * @param employeeId 従業員ID
     * @return 勤怠承認申請リスト
     */
    public List<AttendanceRequest> getAttendanceRequestsByEmployeeId(Integer employeeId);

}
