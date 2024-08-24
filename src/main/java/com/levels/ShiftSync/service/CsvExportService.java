package com.levels.ShiftSync.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRecord;

@Service
public interface CsvExportService {
    /**
     * 勤怠記録のリストをCSV形式の文字列に変換します。
     *
     * @param records CSVに変換する勤怠記録のリスト
     * @return CSV形式の文字列
     */
    String exportToCsv(List<AttendanceRecord> records);
}
