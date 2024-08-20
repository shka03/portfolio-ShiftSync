package com.levels.ShiftSync.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* ユーザーの認証情報を表すUserDetails実装クラス
*/
@EqualsAndHashCode(callSuper=false)
@Data
public class LoginUser extends User {
	private Integer employeeId;
	
    public LoginUser(
            Integer employeeId,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.employeeId = employeeId;
    }
}
