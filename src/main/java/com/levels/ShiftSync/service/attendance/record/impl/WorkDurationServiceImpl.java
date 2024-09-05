package com.levels.ShiftSync.service.attendance.record.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.LoginUser;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.attendance.record.WorkDurationService;
import com.levels.ShiftSync.utility.SecurityUtils;

@Service
public class WorkDurationServiceImpl implements WorkDurationService {
	
	@Autowired
	private AttendanceRecordMapper attendanceRecordMapper;

    /**
     * 当日の出退勤レコードに基づいて、当日の勤務時間を登録または更新します。
     */
    @Override
    public void upsertTodayWorkDuration() {
        List<AttendanceRecord> todayRecord = getTodayRecordForEmployee();
        AttendanceRecord firstRecord = todayRecord.get(0);  // リストの最初の要素を取得
        attendanceRecordMapper.upsertWorkDuration(firstRecord.getRecordId(), firstRecord.getClockOut(), firstRecord.getClockIn());
    }

    /**
     * 出退勤レコードの勤務時間を新しい出勤・退勤時刻で再計算し、登録または更新します。
     * 
     * @param recordId 出退勤レコードのID
     * @param newClockOut 新しい退勤時刻
     * @param newClockIn 新しい出勤時刻
     */
    @Override
    public void upsertWorkDuration(Integer recordId, Timestamp newClockOut, Timestamp newClockIn) {
        attendanceRecordMapper.upsertWorkDuration(recordId, newClockOut, newClockIn);
    }

    /**
     * 指定した月の出退勤記録を取得します。
     * 
     * @param month 取得したい月（1 = 1月、12 = 12月）
     * @return 指定した月の出退勤記録のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getRecordForYearByMonth(int month) {
        // 現在のユーザーIDを取得
        Integer employeeId = ((LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmployeeId();

        // 現在の年と指定した月を "YYYY-MM" 形式で取得
        Calendar cal = Calendar.getInstance();
        String yearMonth = String.format("%d-%02d", cal.get(Calendar.YEAR), month);

        return attendanceRecordMapper.getRecordForYearByMonth(employeeId, yearMonth);
    }
	
    /**
     * 現在ログインしている従業員の当日の出退勤レコードを取得します。
     * 
     * @return 当日の出退勤レコードのリスト
     */
    @Override
    public List<AttendanceRecord> getTodayRecordForEmployee() {
        Integer employeeId = SecurityUtils.getEmployeeIdFromSecurityContext();
        return attendanceRecordMapper.getTodayRecordForEmployee(employeeId);
    }

}
