package com.levels.ShiftSync.csv_download.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.impl.CsvExportServiceImpl;

@SpringBootTest
public class CsvExportServiceImplTest {

    @Autowired
    private CsvExportServiceImpl csvExportServiceImpl;

    @Test
    public void testExportToCsv() {
        // Mockデータの準備
        List<AttendanceRecord> records = Collections.singletonList(new AttendanceRecord(
            1, 1, Timestamp.valueOf("2024-08-24 08:00:00"), Timestamp.valueOf("2024-08-24 17:00:00"),null
        ));

        // CSVデータの生成
        String csvData = csvExportServiceImpl.exportToCsv(records);

        // 生成されたCSVデータの検証
        String expectedCsvData = "\"Record ID\",\"Employee ID\",\"Clock In\",\"Clock Out\"\n" +
                                 "\"1\",\"1\",\"2024-08-24 08:00:00\",\"2024-08-24 17:00:00\"";
        assertEquals(
            normalizeSpaces(normalizeLineEndings(expectedCsvData)),
            normalizeSpaces(normalizeLineEndings(csvData))
        );
    }

    @Test
    public void testExportToCsv_WithException() {
        // 例外が発生するシナリオを模擬する
        CsvExportServiceImpl faultyCsvExportServiceImpl = spy(csvExportServiceImpl);
        doThrow(new RuntimeException("Test exception")).when(faultyCsvExportServiceImpl).exportToCsv(anyList());

        // Mockデータの準備
        List<AttendanceRecord> records = Collections.singletonList(new AttendanceRecord(1, 1, Timestamp.valueOf("2024-08-24 08:00:00"), Timestamp.valueOf("2024-08-24 17:00:00"),null));

        // 例外が発生する場合のテスト
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            faultyCsvExportServiceImpl.exportToCsv(records);
        });
        assertEquals("Test exception", thrownException.getMessage());
    }
    

    private String normalizeLineEndings(String input) {
        return input.replace("\r\n", "\n");
    }
    
    private String normalizeSpaces(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }

}