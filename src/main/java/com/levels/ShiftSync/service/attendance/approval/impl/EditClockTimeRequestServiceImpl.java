package com.levels.ShiftSync.service.attendance.approval.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.levels.ShiftSync.entity.AttendanceEditRequest;
import com.levels.ShiftSync.repository.attendance.approval.EditClockTimeRequestMapper;
import com.levels.ShiftSync.service.attendance.approval.EditClockTimeRequestService;

@Service
@Transactional
public class EditClockTimeRequestServiceImpl implements EditClockTimeRequestService {

	@Autowired
	private EditClockTimeRequestMapper editClockTimeRequestMapper;
	
	@Override
	public List<AttendanceEditRequest> getAllRequests() {
		return editClockTimeRequestMapper.getAllRequests();
	}

	@Override
	public List<AttendanceEditRequest> getCurrentEditRecord(Integer employeeId, String yearMonthDay) {
		return editClockTimeRequestMapper.getCurrentEditRecord(employeeId, yearMonthDay);
	}

	@Override
	public void updateEditClockTimeRecord(Integer recordId, Integer employeeId, Timestamp newClockInTime, Timestamp newClockOutTime, String newWorkDuration) {
		editClockTimeRequestMapper.updateEditClockTimeRecord(recordId, employeeId, newClockInTime, newClockOutTime, newWorkDuration);
	}

	@Override
	public void deleteEditClockTimeApproval(Integer employeeId, String yearMonthDay) {
		editClockTimeRequestMapper.deleteEditClockTimeApproval(employeeId, yearMonthDay);
	}

}
