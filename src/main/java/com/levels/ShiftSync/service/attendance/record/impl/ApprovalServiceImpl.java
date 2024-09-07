package com.levels.ShiftSync.service.attendance.record.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.repository.attendance.record.RecordMapper;
import com.levels.ShiftSync.service.attendance.record.ApprovalService;

@Service
public class ApprovalServiceImpl implements ApprovalService {

	@Autowired
	private RecordMapper attendanceRecordMapper;
	
    /**
     * 指定した従業員の指定した月の勤怠承認申請を登録します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 承認対象の年月（YYYY-MM形式）
     */
    @Override
    public void insertApproveRequest(Integer employeeId, String yearMonth) {
        attendanceRecordMapper.insertApproveRequest(employeeId, yearMonth);
    }

    /**
     * 指定した年月の勤怠承認申請がないかを確認します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 確認する年月（YYYY-MM形式）
     * @return 勤怠承認申請がない場合はtrue、それ以外はfalse
     */
	@Override
    public boolean isNoRequest(Integer employeeId, String yearMonth) {
        return attendanceRecordMapper.isNoRequest(employeeId, yearMonth);
    }

    /**
     * 指定した年月に勤怠承認申請が存在するかを確認します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 確認する年月（YYYY-MM形式）
     * @return 勤怠承認申請がある場合はtrue、それ以外はfalse
     */
	@Override
    public boolean hasRequestsForMonth(Integer employeeId, String yearMonth) {
        List<AttendanceRequest> records = attendanceRecordMapper.getRequestsForMonth(employeeId, yearMonth);
        return !records.isEmpty();
    }

}
