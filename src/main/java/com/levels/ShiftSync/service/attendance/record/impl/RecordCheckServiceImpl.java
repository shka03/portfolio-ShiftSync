package com.levels.ShiftSync.service.attendance.record.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.repository.attendance.record.RecordMapper;
import com.levels.ShiftSync.service.attendance.record.RecordCheckService;

@Service
public class RecordCheckServiceImpl implements RecordCheckService {
	
	@Autowired
	private RecordMapper attendanceRecordMapper;

    /**
     * 指定した年月に出退勤記録が存在するかを確認します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 確認する年月（YYYY-MM形式）
     * @return 出退勤記録がある場合はtrue、それ以外はfalse
     */
	@Override
    public boolean hasRecordsForMonth(Integer employeeId, String yearMonth) {
        List<AttendanceRecord> records = attendanceRecordMapper.getRecordForYearByMonth(employeeId, yearMonth);
        return !records.isEmpty();
    }

}
