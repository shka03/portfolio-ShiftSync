package com.levels.ShiftSync.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.levels.ShiftSync.entity.Authentication;
import com.levels.ShiftSync.entity.LoginUser;
import com.levels.ShiftSync.entity.Role;
import com.levels.ShiftSync.repository.AuthenticationMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUserDetailsServiceImpl implements UserDetailsService {
    private final AuthenticationMapper authenticationMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // usernameをemployeeIdと見なして処理
        return loadUserByEmployeeId(username);
    }

    public UserDetails loadUserByEmployeeId(String employeeId) throws UsernameNotFoundException {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new UsernameNotFoundException("従業員IDが空です");
        }
    	
    	// 「認証テーブル」からデータを取得
        Authentication authentication = authenticationMapper.selectByEmployeeId(Integer.valueOf(employeeId));
        
        // 対象データがあれば、UserDetailsの実装クラスを返す
        if (authentication != null) {
            // 対象データが存在する
            // UserDetailsの実装クラスを返す
            return new LoginUser(
            		this.changeTypeEmployeeId(employeeId), 
                    authentication.getUsername(), 
                    authentication.getPassword(), 
                    getAuthorityList(authentication.getAuthority()));
        } else {
            // 対象データが存在しない
            throw new UsernameNotFoundException(
            employeeId + " => 指定している従業員IDは存在しません");
        }
    }
    
    private Integer changeTypeEmployeeId(String employeeId) {
    	return Integer.valueOf(employeeId);
    }

    private List<GrantedAuthority> getAuthorityList(Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        if (role == Role.ADMIN) {
            authorities.add(new SimpleGrantedAuthority(Role.USER.name()));
        }
        return authorities;
    }
}
