package com.levels.ShiftSync.service.attendance.record.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.attendance.record.ClockInService;
import com.levels.ShiftSync.utility.SecurityUtils;

@Service
public class ClockInServiceImpl implements ClockInService {
	
	@Autowired
	private AttendanceRecordMapper attendanceRecordMapper;

    /**
     * 現在の時刻で退勤時間を記録します。
     */
    @Override
    public void clockInTime() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(SecurityUtils.getEmployeeIdFromSecurityContext());
        record.setClockIn(new Timestamp(System.currentTimeMillis()));
        attendanceRecordMapper.clockIn(record);
    }
    
    /**
     * 指定されたレコードの出勤時間を更新し、出退勤時間を再計算します。
     * 
     * @param recordId 出退勤レコードのID
     * @param employeeId 従業員のID
     * @param newClockIn 新しい出勤時刻
     */
    @Override
    public void updateClockInTime(Integer recordId, Integer employeeId, Timestamp newClockIn) {
        Map<String, Object> params = new HashMap<>();
        params.put("recordId", recordId);
        params.put("employeeId", employeeId);
        params.put("newClockIn", newClockIn);

        // 出勤時間を更新
        attendanceRecordMapper.updateClockInTime(params);

        // 勤怠時間を再計算
        Timestamp currClockOut = getCurrentRecord(recordId).getClockOut();
        attendanceRecordMapper.upsertWorkDuration(recordId, currClockOut, newClockIn);
    }
    
    /**
     * 指定されたレコードIDに基づいて、現在の出退勤レコードを取得します。
     * 
     * @param recordId 出退勤レコードのID
     * @return 現在の出退勤レコード
     */
    private AttendanceRecord getCurrentRecord(Integer recordId) {
        return attendanceRecordMapper.getCurrentRecord(recordId);
    }

}
