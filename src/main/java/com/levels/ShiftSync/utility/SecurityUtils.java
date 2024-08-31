package com.levels.ShiftSync.utility;

import org.springframework.security.core.context.SecurityContextHolder;

import com.levels.ShiftSync.entity.LoginUser;

public class SecurityUtils {

    /**
     * 認証情報から従業員IDを取得するメソッド
     * @return 現在認証されているユーザーの従業員ID
     */
    public static Integer getEmployeeIdFromSecurityContext() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser.getEmployeeId();
    }
}
