package com.levels.ShiftSync.service.attendance.record.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.attendance.record.ClockInMapper;
import com.levels.ShiftSync.repository.attendance.record.RecordMapper;
import com.levels.ShiftSync.service.attendance.record.RecordService;
import com.levels.ShiftSync.utility.SecurityUtils;

@Service
public class ClockInServiceImpl implements RecordService {
	
	@Autowired
	private ClockInMapper clockInMapper;

	@Autowired
	private RecordMapper recordMapper;
	
    /**
     * 現在の時刻で退勤時間を記録します。
     */
    @Override
    public void insert() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(SecurityUtils.getEmployeeIdFromSecurityContext());
        record.setClockIn(new Timestamp(System.currentTimeMillis()));
        clockInMapper.insert(record);
    }
    
    /**
     * 指定されたレコードの出勤時間を更新し、出退勤時間を再計算します。
     * 
     * @param recordId 出退勤レコードのID
     * @param employeeId 従業員のID
     * @param newClockIn 新しい出勤時刻
     */
    @Override
    public void update(Integer recordId, Integer employeeId, Timestamp newClockIn) {
        Map<String, Object> params = new HashMap<>();
        params.put("recordId", recordId);
        params.put("employeeId", employeeId);
        params.put("newClockIn", newClockIn);

        // 出勤時間を更新
        clockInMapper.update(params);

        // 勤怠時間を再計算
        Timestamp currClockOut = getCurrentRecord(recordId).getClockOut();
        recordMapper.upsertWorkDuration(recordId, currClockOut, newClockIn);
    }
    
    /**
     * 指定されたレコードIDに基づいて、現在の出退勤レコードを取得します。
     * 
     * @param recordId 出退勤レコードのID
     * @return 現在の出退勤レコード
     */
    private AttendanceRecord getCurrentRecord(Integer recordId) {
        return clockInMapper.getCurrentRecord(recordId);
    }

}
