package com.levels.ShiftSync.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {
    private Integer requestId;
    private Integer employeeId;
    private String yearMonth;
    private String status;
}