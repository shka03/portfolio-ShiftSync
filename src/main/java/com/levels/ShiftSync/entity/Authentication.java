package com.levels.ShiftSync.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authentication {
	/** 従業員ID　*/
	private Integer employeeId;
	/** ユーザー名 */
	private String username;
	/** パスワード */
	private String password;
	/** 権限 */
	private Role authority;
}
