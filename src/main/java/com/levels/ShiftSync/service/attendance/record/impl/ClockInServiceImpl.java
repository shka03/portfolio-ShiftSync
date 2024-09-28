package com.levels.ShiftSync.service.attendance.record.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.attendance.record.ClockInMapper;
import com.levels.ShiftSync.service.attendance.record.RecordService;
import com.levels.ShiftSync.utility.SecurityUtils;

@Service
@Transactional
public class ClockInServiceImpl implements RecordService {
	
	@Autowired
	private ClockInMapper clockInMapper;

    /**
     * 現在の時刻で出勤時間を記録します。
     */
    @Override
    public void insert() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(SecurityUtils.getEmployeeIdFromSecurityContext());
        record.setClockIn(new Timestamp(System.currentTimeMillis()));
        clockInMapper.insert(record);
    }
    
    /**
     * 指定されたレコードIDに基づいて、現在の出退勤レコードを取得します。
     * 
     * @param recordId 出退勤レコードのID
     * @return 現在の出退勤レコード
     */
    @Override
    public AttendanceRecord getCurrentRecord(Integer recordId) {
        return clockInMapper.getCurrentRecord(recordId);
    }

}
