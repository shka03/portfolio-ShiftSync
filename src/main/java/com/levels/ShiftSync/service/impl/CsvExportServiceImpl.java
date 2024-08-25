package com.levels.ShiftSync.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.CsvExportService;
import com.opencsv.CSVWriter;

@Service
public class CsvExportServiceImpl implements CsvExportService {

    // DateTimeFormatterを用いて、日付を"yyyy-MM-dd HH:mm:ss"の形式でフォーマットする
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 勤怠記録のリストをCSV形式の文字列に変換します。
     *
     * @param records CSVに変換する勤怠記録のリスト
     * @return 生成されたCSVデータを含む文字列。エラーが発生した場合はエラーメッセージを含む文字列を返します。
     */
    @Override
    public String exportToCsv(List<AttendanceRecord> records) {
        // CSVデータを保持するためのStringWriterを作成
        StringWriter stringWriter = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            // ヘッダーの書き込み
            csvWriter.writeNext(new String[]{"Record ID", "Employee ID", "Clock In", "Clock Out"});
            
            // 勤怠記録の各データ行をCSVに書き込む
            for (AttendanceRecord record : records) {
                // Clock InとClock OutのTimestampをフォーマット済みの文字列に変換
                String clockInFormatted = formatTimestamp(record.getClockIn());
                String clockOutFormatted = record.getClockOut() != null ? formatTimestamp(record.getClockOut()) : "";
                
                // 変換されたデータをCSVの行として書き込む
                csvWriter.writeNext(new String[]{
                        record.getRecordId().toString(),
                        record.getEmployeeId().toString(),
                        clockInFormatted,
                        clockOutFormatted
                });
            }
        } catch (IOException e) {
            // 例外が発生した場合の処理：エラーメッセージをログに出力し、エラーメッセージを返す
            e.printStackTrace();
            return "Error generating CSV: " + e.getMessage();
        }
        // 生成されたCSVデータを文字列として返す
        return stringWriter.toString();
    }

    /**
     * TimestampをLocalDateTimeに変換し、指定のフォーマットで文字列に変換します。
     *
     * @param timestamp フォーマットするTimestampオブジェクト
     * @return フォーマットされた日付時刻の文字列
     */
    private String formatTimestamp(Timestamp timestamp) {
        // TimestampをLocalDateTimeに変換
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        // LocalDateTimeをフォーマット済みの文字列に変換して返す
        return localDateTime.format(formatter);
    }
}

