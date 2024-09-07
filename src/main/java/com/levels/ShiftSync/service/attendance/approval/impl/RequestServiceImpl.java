package com.levels.ShiftSync.service.attendance.approval.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.repository.AttendanceRequestMapper;
import com.levels.ShiftSync.service.attendance.approval.RequestService;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private AttendanceRequestMapper attendanceRequestMapper;

    @Override
    public List<AttendanceRequest> getAllRequests() {
        return attendanceRequestMapper.getAllRequests();
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
