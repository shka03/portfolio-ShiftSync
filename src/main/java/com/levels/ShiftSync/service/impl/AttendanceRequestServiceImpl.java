package com.levels.ShiftSync.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.repository.AttendanceRequestMapper;
import com.levels.ShiftSync.service.AttendanceRequestService;

@Service
public class AttendanceRequestServiceImpl implements AttendanceRequestService {

    @Autowired
    private AttendanceRequestMapper attendanceRequestMapper;
    
    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    @Override
    public List<AttendanceRequest> getAllRequests() {
        return attendanceRequestMapper.getAllRequests();
    }

    @Override
	public List<AttendanceRecord> getEmployeeMonthRequests(Integer employeeId, String yearMonth) {
        return attendanceRecordMapper.getMonthlyAttendanceForYear(employeeId, yearMonth);
    }
    
    @Override
    public String getApprovalStatus(Integer employeeId, String yearMonth) {
    	return attendanceRequestMapper.getApprovalStatus(employeeId, yearMonth);
    }
    
    @Override
    public void updateApproveStatus(Integer employeeId, String yearMonth, String status) {
    	attendanceRequestMapper.updateApproveStatus(employeeId, yearMonth, status);
    }
    
    @Override
    public void deleteRequest(Integer employeeId, String yearMonth) {
    	attendanceRequestMapper.deleteRequest(employeeId, yearMonth);
    }
    
}
