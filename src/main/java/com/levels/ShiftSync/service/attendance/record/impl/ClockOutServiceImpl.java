package com.levels.ShiftSync.service.attendance.record.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.attendance.record.ClockOutMapper;
import com.levels.ShiftSync.service.attendance.record.RecordService;
import com.levels.ShiftSync.utility.SecurityUtils;

@Service
@Transactional
public class ClockOutServiceImpl implements RecordService {

	@Autowired
	private ClockOutMapper clockOutMapper;

    /**
     * 従業員の退勤時間を現在の時刻で記録します。
     * 現在ログインしている従業員のIDを使用して、新しい退勤レコードをデータベースに保存します。
     */
    @Override
    public void insert() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(SecurityUtils.getEmployeeIdFromSecurityContext());
        record.setClockOut(new Timestamp(System.currentTimeMillis()));
        clockOutMapper.insert(record);
    }
    
    /**
     * 指定されたレコードIDに基づいて、現在の出退勤レコードを取得します。
     * 
     * @param recordId 出退勤レコードのID
     * @return 現在の出退勤レコード
     */
    @Override
    public AttendanceRecord getCurrentRecord(Integer recordId) {
        return clockOutMapper.getCurrentRecord(recordId);
    }

}
