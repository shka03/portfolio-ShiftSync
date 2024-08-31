package com.levels.ShiftSync.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.levels.ShiftSync.entity.AttendanceRequest;

@Mapper
public interface AttendanceRequestMapper {

    /**
     * 全ての勤怠承認申請を取得するメソッド
     * @return 勤怠承認申請リスト
     */
    List<AttendanceRequest> getAllAttendanceRequests();

    /**
     * 従業員IDに基づいて勤怠承認申請を取得するメソッド
     * @param employeeId 従業員ID
     * @return 勤怠承認申請リスト
     */
    List<AttendanceRequest> getAttendanceRequestsByEmployeeId(@Param("employeeId") Integer employeeId);
}
