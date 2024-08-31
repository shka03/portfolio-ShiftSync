package com.levels.ShiftSync.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.AttendanceRequest;

@Mapper
public interface AttendanceRequestMapper {

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
    List<AttendanceRecord> getEmployeeMonthRequests(
    		@Param("employeeId") Integer employeeId,
    		@Param("yearMonth") String yearMonth);

    /**
     * 従業員IDと指定年月に基づいて勤怠申請の承認状態を変更するメソッド
     * @param employeeId 従業員ID
     * @param yearMonth 年月
     */
    void updateApproveStatus(
    		@Param("employeeId") Integer employeeId,
    		@Param("yearMonth") String yearMonth,
    		@Param("status") String status);
    
    /**
     * 従業員IDと指定年月に基づいて勤怠承認申請を取得するメソッド
     * @param employeeId 従業員ID
     * @param yearMonth 年月
     * @return 従業員の指定年月の承認状態
     */
    String getApprovalStatus(Integer employeeId, String yearMonth);
}
