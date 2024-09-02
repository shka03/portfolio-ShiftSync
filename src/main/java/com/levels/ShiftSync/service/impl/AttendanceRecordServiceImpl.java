package com.levels.ShiftSync.service.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.entity.LoginUser;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.AttendanceRecordService;
import com.levels.ShiftSync.utility.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    private final AttendanceRecordMapper attendanceRecordMapper;

    /**
     * 従業員の出勤時間を現在の時刻で記録します。
     * 現在ログインしている従業員のIDを使用して、新しい出勤レコードをデータベースに保存します。
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
     * 指定した従業員の指定した月の勤怠承認申請を登録します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 承認対象の年月（YYYY-MM形式）
     */
    @Override
    public void insertApproveRequest(Integer employeeId, String yearMonth) {
        attendanceRecordMapper.insertApproveRequest(employeeId, yearMonth);
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

    /**
     * 指定した月の出退勤記録を取得します。
     * 
     * @param month 取得したい月（1 = 1月、12 = 12月）
     * @return 指定した月の出退勤記録のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
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
     * 指定した年月の勤怠承認申請がないかを確認します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 確認する年月（YYYY-MM形式）
     * @return 勤怠承認申請がない場合はtrue、それ以外はfalse
     */
    public boolean isNoRequest(Integer employeeId, String yearMonth) {
        return attendanceRecordMapper.isNoRequest(employeeId, yearMonth);
    }

    /**
     * 指定した年月に勤怠承認申請が存在するかを確認します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 確認する年月（YYYY-MM形式）
     * @return 勤怠承認申請がある場合はtrue、それ以外はfalse
     */
    public boolean hasRequestsForMonth(Integer employeeId, String yearMonth) {
        List<AttendanceRequest> records = attendanceRecordMapper.getRequestsForMonth(employeeId, yearMonth);
        return !records.isEmpty();
    }

    /**
     * 指定した年月に出退勤記録が存在するかを確認します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 確認する年月（YYYY-MM形式）
     * @return 出退勤記録がある場合はtrue、それ以外はfalse
     */
    public boolean hasRecordsForMonth(Integer employeeId, String yearMonth) {
        List<AttendanceRecord> records = attendanceRecordMapper.getRecordForYearByMonth(employeeId, yearMonth);
        return !records.isEmpty();
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
