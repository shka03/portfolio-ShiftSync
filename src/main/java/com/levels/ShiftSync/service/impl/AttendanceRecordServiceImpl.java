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
import com.levels.ShiftSync.entity.LoginUser;
import com.levels.ShiftSync.repository.AttendanceRecordMapper;
import com.levels.ShiftSync.service.AttendanceRecordService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    private final AttendanceRecordMapper attendanceRecordMapper;

    /**
     * 出勤時間をデータベースに挿入するメソッド
     * 現在の時刻を出勤時間として設定し、データベースに保存します。
     */
    @Override
    public void clockInTime() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(getEmployeeIdFromSecurityContext());
        record.setClockIn(new Timestamp(System.currentTimeMillis()));
        attendanceRecordMapper.clockIn(record);
    }
    
    /**
     * 出勤時刻を更新するメソッド
     * 
     * @param recordId 出退勤レコードのID。どのレコードを更新するかを特定するために使用します。
     * @param employeeId 従業員のID。どの従業員の出勤時刻を更新するかを特定するために使用します。
     * @param newClockIn 新しい出勤時刻。タイムスタンプ形式（yyyy-MM-dd HH:mm:ss）で指定します。
     */
    @Override
    public void updateClockInTime(Integer recordId, Integer employeeId, Timestamp newClockIn) {
        Map<String, Object> params = new HashMap<>();
        params.put("recordId", recordId);
        params.put("employeeId", employeeId);
        params.put("newClockIn", newClockIn);

        attendanceRecordMapper.updateClockInTime(params);
    }

    /**
     * 退勤時間をデータベースに挿入するメソッド
     * 現在の時刻を退勤時間として設定し、データベースに保存します。
     */
    @Override
    public void clockOutTime() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(getEmployeeIdFromSecurityContext());
        record.setClockOut(new Timestamp(System.currentTimeMillis()));
        attendanceRecordMapper.clockOut(record);
    }
    
    /**
     * 退勤時刻を更新するメソッド
     * 
     * @param recordId 出退勤レコードのID。どのレコードを更新するかを特定するために使用します。
     * @param employeeId 従業員のID。どの従業員の退勤時刻を更新するかを特定するために使用します。
     * @param newClockOut 新しい退勤時刻。タイムスタンプ形式（yyyy-MM-dd HH:mm:ss）で指定します。
     */
    @Override
    public void updateClockOutTime(Integer recordId, Integer employeeId, Timestamp newClockOut) {
        Map<String, Object> params = new HashMap<>();
        params.put("recordId", recordId);
        params.put("employeeId", employeeId);
        params.put("newClockOut", newClockOut);

        attendanceRecordMapper.updateClockOutTime(params);
    }

    /**
     * 従業員の当日の出退勤時間を取得するメソッド
     * @return 当日の出退勤時間のリスト
     */
    @Override
    public List<AttendanceRecord> getTodayAttendance() {
        Integer employeeId = getEmployeeIdFromSecurityContext();
        return attendanceRecordMapper.getTodayAttendance(employeeId);
    }
    
    /**
     * 任意の月の勤怠記録を取得するメソッド
     * @param month 取得したい月 (1月 = 1, 12月 = 12)
     * @return 指定された月の出退勤時間のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getYearlyAttendanceForMonth(int month) {
        // ログイン中のユーザーIDを取得
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer employeeId = loginUser.getEmployeeId();

        // 現在の年と選択された月を "YYYY-MM" 形式で取得
        Calendar cal = Calendar.getInstance();
        String yearMonth = String.format("%d-%02d", cal.get(Calendar.YEAR), month);

        // MyBatisで勤怠記録を取得
        return attendanceRecordMapper.getMonthlyAttendanceForYear(employeeId, yearMonth);
    }

    /**
     * 認証情報から従業員IDを取得するメソッド
     * @return 現在認証されているユーザーの従業員ID
     */
    private Integer getEmployeeIdFromSecurityContext() {
        // SecurityContextから認証情報を取得
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // LoginUserから従業員IDを取得
        return loginUser.getEmployeeId();
    }
}
