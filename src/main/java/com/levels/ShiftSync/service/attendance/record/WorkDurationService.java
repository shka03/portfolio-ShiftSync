package com.levels.ShiftSync.service.attendance.record;

import java.sql.Timestamp;
import java.util.List;

import com.levels.ShiftSync.entity.AttendanceRecord;

/**
 * 勤務時間の登録・更新に関する操作を管理するサービスインターフェース。
 */
public interface WorkDurationService {

    /**
     * 当日の出退勤レコードに基づいて、当日の勤務時間を登録または更新します。
     */
    void upsertTodayWorkDuration();

    /**
     * 出退勤レコードの勤務時間を新しい出勤・退勤時刻で再計算し、登録または更新します。
     * 
     * @param recordId 出退勤レコードのID
     * @param newClockOut 新しい退勤時刻
     * @param newClockIn 新しい出勤時刻
     */
    void upsertWorkDuration(Integer recordId, Timestamp newClockOut, Timestamp newClockIn);

    /**
     * 指定した月の出退勤記録を取得します。
     * 
     * @param month 取得したい月
     * @return 指定した月の出退勤記録のリスト。出勤または退勤記録がない場合は空のリストを返します。
     */
    List<AttendanceRecord> getRecordForYearByMonth(int month);
    
    /**
     * 現在ログインしている従業員の当日の出退勤レコードを取得します。
     * 
     * @return 当日の出退勤レコードのリスト
     */
    public List<AttendanceRecord> getTodayRecordForEmployee();
}
