package com.levels.ShiftSync.service.attendance.record.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.attendance.record.EditClockTimeMapper;
import com.levels.ShiftSync.service.attendance.record.EditClockTimeService;

@Service
@Transactional
public class EditClockTimeServiceImpl implements EditClockTimeService {

	@Autowired
    private EditClockTimeMapper editClockTimeMapper;

	@Override
	public void updateClockInAndOut(
			Integer recordId,
			Integer employeeId,
			String yearMonthDay,
			Timestamp newClockIn,
			Timestamp newClockOut,
			String applicationReason) {

		String workDuration = calculateWorkDuration(newClockIn, newClockOut);
        editClockTimeMapper.upsertClockInAndOut(recordId, employeeId, yearMonthDay, newClockIn, newClockOut, workDuration, applicationReason);
        
	}

	@Override
	public AttendanceRecord getCurrentRecord(Integer recordId) {
		return editClockTimeMapper.getCurrentRecord(recordId);
	}

	private String calculateWorkDuration(Timestamp clockIn, Timestamp clockOut) {
	    // 勤務時間の計算ロジックを追加
	    long duration = clockOut.getTime() - clockIn.getTime();
	    return String.format("%02d:%02d:%02d", 
	        (duration / (1000 * 60 * 60)) % 24, 
	        (duration / (1000 * 60)) % 60, 
	        (duration / 1000) % 60);
	}

}
