package com.levels.ShiftSync.service.attendance.record.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.attendance.record.ClockOutMapper;
import com.levels.ShiftSync.repository.attendance.record.RecordMapper;
import com.levels.ShiftSync.service.attendance.record.RecordService;
import com.levels.ShiftSync.utility.SecurityUtils;

@Service
public class ClockOutServiceImpl implements RecordService {

	@Autowired
	private ClockOutMapper clockOutMapper;
	
	@Autowired
	private RecordMapper attendanceRecordMapper;

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
     * 指定されたレコードの退勤時間を更新し、出退勤時間を再計算します。
     * 
     * @param recordId 出退勤レコードのID
     * @param employeeId 従業員のID
     * @param newClockOut 新しい退勤時刻
     */
    @Override
    public void update(Integer recordId, Integer employeeId, Timestamp newClockOut) {
        Map<String, Object> params = new HashMap<>();
        params.put("recordId", recordId);
        params.put("employeeId", employeeId);
        params.put("newClockOut", newClockOut);

        // 退勤時間を更新
        clockOutMapper.update(params);

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
    @Override
    public AttendanceRecord getCurrentRecord(Integer recordId) {
        return clockOutMapper.getCurrentRecord(recordId);
    }

}
