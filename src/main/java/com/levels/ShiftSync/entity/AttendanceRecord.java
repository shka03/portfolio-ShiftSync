package com.levels.ShiftSync.entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 従業員の勤怠記録を表すエンティティクラス
 * このクラスは、出勤時間と退勤時間を含む勤怠記録を保持します。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {
    private Integer recordId;
    private Integer employeeId;
    private Timestamp clockIn;
    private Timestamp clockOut;
    private String workDuration;
}
