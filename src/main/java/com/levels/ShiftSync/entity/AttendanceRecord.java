package com.levels.ShiftSync.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {
	private Integer recordId;
    private Integer employeeId;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
}
