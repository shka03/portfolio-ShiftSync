package com.levels.ShiftSync.service.attendance.record.impl;

import java.sql.Timestamp;
import java.time.Duration;

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
		
        // 出勤時間と退勤時間を同時に更新
//        editClockTimeMapper.updateClockInAndOut(recordId, employeeId, newClockIn, newClockOut);

        // 勤務時間の再計算
//        workDurationMapper.upsertWorkDuration(recordId, newClockOut, newClockIn);

		String workDuration = calculateWorkDuration(newClockIn, newClockOut);
        editClockTimeMapper.upsertClockInAndOut(recordId, employeeId, yearMonthDay, newClockIn, newClockOut, workDuration, applicationReason, "申請済");
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
	
	private Duration convertWorkDurationToDuration(String workDuration) {
	    return parseDuration(workDuration); // hh:mm:ss形式のStringをDurationに変換
	}
	
	private Duration parseDuration(String durationString) {
	    // "hh:mm:ss"形式の文字列をパースしてDurationに変換
	    String[] parts = durationString.split(":");
	    
	    long hours = Long.parseLong(parts[0]);
	    long minutes = Long.parseLong(parts[1]);
	    long seconds = Long.parseLong(parts[2]);

	    return Duration.ofHours(hours)
	                   .plusMinutes(minutes)
	                   .plusSeconds(seconds);
	}
}
