package com.levels.ShiftSync.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.repository.AttendanceRequestMapper;

@Service
public class AttendanceRequestServiceImpl {

    @Autowired
    private AttendanceRequestMapper attendanceRequestMapper;

    /**
     * 全ての勤怠承認申請を取得するメソッド
     * @return 勤怠承認申請リスト
     */
    public List<AttendanceRequest> getAllAttendanceRequests() {
        return attendanceRequestMapper.getAllAttendanceRequests();
    }

    /**
     * 従業員IDに基づいて勤怠承認申請を取得するメソッド
     * @param employeeId 従業員ID
     * @return 勤怠承認申請リスト
     */
    public List<AttendanceRequest> getAttendanceRequestsByEmployeeId(Integer employeeId) {
        return attendanceRequestMapper.getAttendanceRequestsByEmployeeId(employeeId);
    }
}
