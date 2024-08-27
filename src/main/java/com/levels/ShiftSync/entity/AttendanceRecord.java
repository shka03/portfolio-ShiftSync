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
    /**
     * 勤怠記録の一意なID
     */
    private Integer recordId;

    /**
     * 従業員のID
     */
    private Integer employeeId;

    /**
     * 出勤時間
     */
    private Timestamp clockIn;

    /**
     * 退勤時間
     */
    private Timestamp clockOut;

    /**
     * 勤務時間
     */
    private String workDuration;
}
