package com.levels.ShiftSync.service.attendance.approval.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.levels.ShiftSync.entity.AttendanceRequest;
import com.levels.ShiftSync.repository.attendance.approval.RequestMapper;
import com.levels.ShiftSync.service.attendance.approval.ApprovalService;

@Service
@Transactional
public class ApprovalServiceImpl implements ApprovalService {

	@Autowired
	private RequestMapper requestMapper;
	
    /**
     * 指定した従業員の指定した月の勤怠承認申請を登録します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 承認対象の年月（YYYY-MM形式）
     */
    @Override
    public void insertApproveRequest(Integer employeeId, String yearMonth) {
    	requestMapper.insertApproveRequest(employeeId, yearMonth);
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
        return requestMapper.isNoRequest(employeeId, yearMonth);
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
        List<AttendanceRequest> records = requestMapper.getRequestsForMonth(employeeId, yearMonth);
        return !records.isEmpty();
    }

}
