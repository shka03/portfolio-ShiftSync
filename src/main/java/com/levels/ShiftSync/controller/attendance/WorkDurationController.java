package com.levels.ShiftSync.controller.attendance;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.levels.ShiftSync.entity.AttendanceRecord;
import com.levels.ShiftSync.service.attendance.record.impl.ApprovalServiceImpl;
import com.levels.ShiftSync.service.attendance.record.impl.RecordCheckServiceImpl;
import com.levels.ShiftSync.service.attendance.record.impl.WorkDurationServiceImpl;
import com.levels.ShiftSync.service.impl.CsvExportServiceImpl;
import com.levels.ShiftSync.utility.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkDurationController {

	private final ApprovalServiceImpl approvalServiceImpl;
	private final CsvExportServiceImpl csvExportServiceImpl;
	private final RecordCheckServiceImpl recordCheckServiceImpl;
    private final WorkDurationServiceImpl workDurationServiceImpl;

    /**
     * 任意の月の勤怠履歴を表示するメソッド
     * @param month 表示する月 (1月 = 1, 12月 = 12)
     * @param model Thymeleafテンプレートにデータを渡すためのモデル
     * @return "attendance-year-month" テンプレート名
     */
    @GetMapping("/attendance-year-month")
    public String showYearMonth(
            @RequestParam(value = "month", required = false) Integer month,
            Model model
    ) {
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }

        List<AttendanceRecord> yearlyAttendance = workDurationServiceImpl.getRecordForYearByMonth(month);

        if (yearlyAttendance.isEmpty()) {
            model.addAttribute("message", "選択された月のデータはありません。");
            model.addAttribute("selectedMonth", month);
            return "attendance-year-month";
        }

        model.addAttribute("attendance_records", yearlyAttendance);
        model.addAttribute("selectedMonth", month);

        Integer employeeId = SecurityUtils.getEmployeeIdFromSecurityContext();
        Calendar cal = Calendar.getInstance();
        String yearMonth = String.format("%d-%02d", cal.get(Calendar.YEAR), month);
        boolean hasRequest = approvalServiceImpl.hasRequestsForMonth(employeeId, yearMonth);
        boolean isNoRequest = approvalServiceImpl.isNoRequest(employeeId, yearMonth);
        model.addAttribute("canApproveRequest", isNoRequest && !hasRequest);

        return "attendance-year-month";
    }

    /**
     * 指定された月の勤怠記録をCSV形式でダウンロードするエンドポイント。
     * @param month CSVを生成する対象の月。指定がない場合は、現在の月が使用される。
     * @return CSVファイルとしてダウンロードできるレスポンス
     */
    @GetMapping("/download-csv")
    public ResponseEntity<InputStreamResource> downloadCsv(
            @RequestParam(value = "month", required = false) Integer month
    ) {
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }

        List<AttendanceRecord> records = workDurationServiceImpl.getRecordForYearByMonth(month);
        String csvData = csvExportServiceImpl.exportToCsv(records);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes());

        HttpHeaders headers = new HttpHeaders();
        String filename = "attendance_records_" + month + ".csv";
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(inputStream));
    }
    
	  /**
	  * 承認申請依頼の処理を行うメソッド
	  * @return リダイレクト先のURL
	  */
	 @PostMapping("/insert-approve-request")
	 public String insertApproveRequest(
	         @RequestParam("month") Integer month,
	         RedirectAttributes redirectAttributes) {
	     Integer employeeId = SecurityUtils.getEmployeeIdFromSecurityContext();
	     String yearMonth = String.format("%d-%02d", LocalDate.now().getYear(), month);
	
	     // 勤怠履歴が存在するかどうか確認
	     if (!recordCheckServiceImpl.hasRecordsForMonth(employeeId, yearMonth)) {
	         redirectAttributes.addFlashAttribute("approveErrorMessage", "指定された年月に勤怠履歴が存在しないため、承認申請ができません。");
	         return "redirect:/attendance-year-month?month=" + month;
	     }
	
	     // 承認申請を実行
	     approvalServiceImpl.insertApproveRequest(employeeId, yearMonth);
	     redirectAttributes.addFlashAttribute("approveSuccessMessage", "承認申請をしました。");
	     return "redirect:/attendance-year-month?month=" + month;
	 }
}

