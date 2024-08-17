package com.levels.ShiftSync.entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {
	private Integer recordId;
    private Integer employeeId;
    private Timestamp clockIn;
    private Timestamp clockOut;
}
