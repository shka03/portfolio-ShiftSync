package com.levels.ShiftSync.service;

import java.util.List;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.AttendanceRequest;

public interface AttendanceRequestService {

    /**
     * 全ての勤怠承認申請を取得するメソッド
     * @return 勤怠承認申請リスト
     */
    List<AttendanceRequest> getAllRequests();

    /**
     * 従業員IDと指定年月に基づいて勤怠承認申請を取得するメソッド
     * @param employeeId 従業員ID
     * @param yearMonth 年月
     * @return 指定年月の勤怠承認申請リスト
     */
    List<AttendanceRecord> getEmployeeMonthRequests(Integer employeeId, String yearMonth);
    
//    /**
//     * 従業員IDと指定年月に基づいて勤怠申請の承認状態を変更するメソッド
//     * @param employeeId 従業員ID
//     * @param yearMonth 年月
//     */
//    void updateApproveStatus(Integer employeeId, String yearMonth);

}
