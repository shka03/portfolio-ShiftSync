package com.levels.ShiftSync.service.attendance.record;

import java.sql.Timestamp;

public interface ClockOutService {
	
    /**
     * 現在の時刻で退勤時間を記録します。
     */
    void clockOutTime();
	
    /**
     * 指定されたレコードの退勤時間を更新し、出退勤時間を再計算します。
     * 
     * @param recordId 出退勤レコードのID
     * @param employeeId 従業員のID
     * @param newClockOut 新しい退勤時刻
     */
    void updateClockOutTime(Integer recordId, Integer employeeId, Timestamp newClockOut);

}
