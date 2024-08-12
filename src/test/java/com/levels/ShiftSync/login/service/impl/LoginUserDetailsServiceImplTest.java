package com.levels.ShiftSync.login.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.levels.ShiftSync.entity.Authentication;
import com.levels.ShiftSync.entity.Role;
import com.levels.ShiftSync.repository.AuthenticationMapper;
import com.levels.ShiftSync.service.impl.LoginUserDetailsServiceImpl;

class LoginUserDetailsServiceImplTest {

    @Mock
    private AuthenticationMapper authenticationMapper;

    @InjectMocks
    private LoginUserDetailsServiceImpl loginUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByEmployeeId_ExistingEmployee() {
        // テスト要約: 存在する従業員IDで正しいUserDetailsを返す
        Authentication authentication = new Authentication();
        authentication.setEmployeeId(1);
        authentication.setUsername("testuser");
        authentication.setPassword("password");
        authentication.setAuthority(Role.ADMIN);

        when(authenticationMapper.selectByEmployeeId(anyInt())).thenReturn(authentication);

        UserDetails userDetails = loginUserDetailsService.loadUserByEmployeeId("1");

        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
    }

    @Test
    void testLoadUserByEmployeeId_NonExistingEmployee() {
        // テスト要約: 存在しない従業員IDでUsernameNotFoundExceptionをスローする
        when(authenticationMapper.selectByEmployeeId(anyInt())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            loginUserDetailsService.loadUserByEmployeeId("999");
        });
    }

    @Test
    void testLoadUserByEmployeeId_EmptyEmployeeId() {
        // テスト要約: 空の従業員IDでUsernameNotFoundExceptionをスローする
        assertThrows(UsernameNotFoundException.class, () -> {
            loginUserDetailsService.loadUserByEmployeeId("");
        });
    }

    @Test
    void testLoadUserByEmployeeId_InvalidEmployeeIdFormat() {
        // テスト要約: 無効な従業員ID形式でNumberFormatExceptionをスローする
        assertThrows(NumberFormatException.class, () -> {
            loginUserDetailsService.loadUserByEmployeeId("invalid");
        });
    }

    @Test
    void testLoadUserByUsername() {
        // テスト要約: loadUserByUsernameメソッドが正常に動作することを確認する
        Authentication authentication = new Authentication();
        authentication.setEmployeeId(1);
        authentication.setUsername("testuser");
        authentication.setPassword("password");
        authentication.setAuthority(Role.ADMIN);

        when(authenticationMapper.selectByEmployeeId(anyInt())).thenReturn(authentication);

        UserDetails userDetails = loginUserDetailsService.loadUserByUsername("1");

        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
    }
}
