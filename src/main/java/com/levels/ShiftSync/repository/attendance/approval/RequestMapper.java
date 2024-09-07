package com.levels.ShiftSync.repository.attendance.approval;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.entity.AttendanceRequest;

@Mapper
public interface RequestMapper {

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
     * 従業員IDと指定年月に基づいて勤怠承認申請を取得するメソッド
     * @param employeeId 従業員ID
     * @param yearMonth 年月
     * @return 従業員の指定年月の承認状態
     */
    String getApprovalStatus(Integer employeeId, String yearMonth);
    
    /**
     * @param employeeId 従業員ID
     * @param yearMonth 年月 (YYYY-MM)
     * @return 指定された月の承認申請リスト。申請がない場合は空のリストを返します。
     */
    List<AttendanceRequest> getRequestsForMonth(Integer employeeId, String yearMonth);
   
    /** 
     * @param employeeId 従業員ID。どの従業に関する勤怠履歴を更新するかを特定するために使用します。
     * @param yearMonth 承認対象の年月。年月（yyyy-MM）形式で指定します。
     */
    void insertApproveRequest(Integer employeeId, String yearMonth);
    
    /**
     * 従業員IDと指定年月に基づいて勤怠申請の承認状態を変更するメソッド
     * @param employeeId 従業員ID
     * @param yearMonth 年月
     * @param status 承認ステータス
     */
    void updateApproveStatus(
    		@Param("employeeId") Integer employeeId,
    		@Param("yearMonth") String yearMonth,
    		@Param("status") String status);
    
    /**
     * 従業員IDと指定年月に基づいて勤怠承認申請を取得するメソッド
     * @param employeeId 従業員ID
     * @param yearMonth 年月
     */
    void deleteRequest(
    		@Param("employeeId") Integer employeeId,
    		@Param("yearMonth") String yearMonth);
    /**
     * @param employeeId 従業員のID
     * @param yearMonth 承認対象の年月。年月（yyyy-MM）形式で指定します。
     * @return 
     */    
    boolean isNoRequest(Integer employeeId, String yearMonth);

}
