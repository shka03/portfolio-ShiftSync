package com.levels.ShiftSync.service.attendance.record;

/**
 * 勤怠承認申請に関する操作を管理するサービスインターフェース。
 */
public interface ApprovalService {

    /**
     * 指定した従業員の指定した月の勤怠承認申請を登録します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 承認対象の年月（YYYY-MM形式）
     */
    void insertApproveRequest(Integer employeeId, String yearMonth);

    /**
     * 指定した年月の勤怠承認申請がないかを確認します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 確認する年月（YYYY-MM形式）
     * @return 勤怠承認申請がない場合はtrue、それ以外はfalse
     */
    boolean isNoRequest(Integer employeeId, String yearMonth);

    /**
     * 指定した年月に勤怠承認申請が存在するかを確認します。
     * 
     * @param employeeId 従業員ID
     * @param yearMonth 確認する年月（YYYY-MM形式）
     * @return 勤怠承認申請がある場合はtrue、それ以外はfalse
     */
    boolean hasRequestsForMonth(Integer employeeId, String yearMonth);
}
