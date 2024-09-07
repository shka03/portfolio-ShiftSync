package com.levels.ShiftSync.service.attendance.approval.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.repository.attendance.approval.RequestMapper;
import com.levels.ShiftSync.service.attendance.approval.RequestService;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestMapper requestMapper;

    @Override
    public List<AttendanceRequest> getAllRequests() {
        return requestMapper.getAllRequests();
    }
    
    @Override
    public String getApprovalStatus(Integer employeeId, String yearMonth) {
    	return requestMapper.getApprovalStatus(employeeId, yearMonth);
    }
    
    @Override
    public void updateApproveStatus(Integer employeeId, String yearMonth, String status) {
    	requestMapper.updateApproveStatus(employeeId, yearMonth, status);
    }
    
    @Override
    public void deleteRequest(Integer employeeId, String yearMonth) {
    	requestMapper.deleteRequest(employeeId, yearMonth);
    }
    
}
