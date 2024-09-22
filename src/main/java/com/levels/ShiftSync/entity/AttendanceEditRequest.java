package com.levels.ShiftSync.entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEditRequest {
    private Integer requestId;
    private Integer recordId;
    private Integer employeeId;
    private String yearMonth;
    private Timestamp clockIn;
    private Timestamp clockOut;
    private String workDuration;
    private String applicationReason;
    private String status;
}
