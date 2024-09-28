package com.levels.ShiftSync.repository.attendance.record;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.levels.ShiftSync.entity.AttendanceEditRequest;
import com.levels.ShiftSync.entity.AttendanceRecord;

@Mapper
public interface EditClockTimeMapper {

    void updateClockInAndOut(@Param("recordId") Integer recordId,
                             @Param("employeeId") Integer employeeId,
                             @Param("newClockIn") Timestamp newClockIn,
                             @Param("newClockOut") Timestamp newClockOut);
    
    void upsertClockInAndOut(@Param("recordId") Integer recordId,
                              @Param("employeeId") Integer employeeId,
                              @Param("yearMonthDay") String yearMonthDay,
                              @Param("newClockIn") Timestamp newClockIn,
                              @Param("newClockOut") Timestamp newClockOut,
                              @Param("workDuration") String workDuration,
                              @Param("applicationReason") String applicationReason);
  
    AttendanceRecord getCurrentRecord(@Param("recordId") Integer recordId);
    
    List<AttendanceEditRequest> getCurrentEditRecord(Integer employeeId, String yearMonthDay);
}
