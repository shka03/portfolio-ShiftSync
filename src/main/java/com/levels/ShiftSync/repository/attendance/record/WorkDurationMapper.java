package com.levels.ShiftSync.repository.attendance.record;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.levels.ShiftSync.entity.AttendanceRecord;

@Mapper
public interface WorkDurationMapper {

    /**
     * @param recordId 出退勤レコードID
     * @param clockIn 出勤時刻
     * @param clockOut 退勤時刻
     */
    void upsertWorkDuration(Integer recordId, Timestamp clockOut, Timestamp clockIn);
    
    /**
     * 現在ログインしている従業員の当日の出退勤レコードを取得します。
     * 
     * @return 当日の出退勤レコードのリスト
     */
    List<AttendanceRecord> getTodayRecordForEmployee(Integer employeeId);
    

    /**
     * 指定した月の出退勤記録を取得します。
     * 
     * @param month 取得したい月（1 = 1月、12 = 12月）
     * @return 指定した月の出退勤記録のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getRecordForYearByMonth(Integer employeeId, String yearMonth);

}
