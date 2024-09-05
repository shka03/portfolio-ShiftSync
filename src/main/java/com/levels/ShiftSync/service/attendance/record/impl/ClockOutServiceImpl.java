package com.levels.ShiftSync.service.attendance.record.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.attendance.record.ClockOutService;
import com.levels.ShiftSync.utility.SecurityUtils;

@Service
public class ClockOutServiceImpl implements ClockOutService {
	
	@Autowired
	private AttendanceRecordMapper attendanceRecordMapper;

    /**
     * 従業員の退勤時間を現在の時刻で記録します。
     * 現在ログインしている従業員のIDを使用して、新しい退勤レコードをデータベースに保存します。
     */
    @Override
    public void clockOutTime() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(SecurityUtils.getEmployeeIdFromSecurityContext());
        record.setClockOut(new Timestamp(System.currentTimeMillis()));
        attendanceRecordMapper.clockOut(record);
    }
    
    /**
     * 指定されたレコードの退勤時間を更新し、出退勤時間を再計算します。
     * 
     * @param recordId 出退勤レコードのID
     * @param employeeId 従業員のID
     * @param newClockOut 新しい退勤時刻
     */
    @Override
    public void updateClockOutTime(Integer recordId, Integer employeeId, Timestamp newClockOut) {
        Map<String, Object> params = new HashMap<>();
        params.put("recordId", recordId);
        params.put("employeeId", employeeId);
        params.put("newClockOut", newClockOut);

        // 退勤時間を更新
        attendanceRecordMapper.updateClockOutTime(params);

        // 勤怠時間を再計算
        Timestamp currClockIn = getCurrentRecord(recordId).getClockIn();
        attendanceRecordMapper.upsertWorkDuration(recordId, newClockOut, currClockIn);
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
