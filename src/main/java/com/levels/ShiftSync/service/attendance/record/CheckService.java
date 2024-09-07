package com.levels.ShiftSync.service.attendance.record;

public interface CheckService {
	
    /**
     * 指定した年月に出退勤記録が存在するかを確認します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 確認する年月（YYYY-MM形式）
     * @return 出退勤記録がある場合はtrue、それ以外はfalse
     */
    boolean hasRecordsForMonth(Integer employeeId, String yearMonth);
    
}
