package com.levels.ShiftSync.login.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.levels.ShiftSync.entity.Authentication;
import com.levels.ShiftSync.entity.Role;
import com.levels.ShiftSync.repository.AuthenticationMapper;
import com.levels.ShiftSync.service.impl.LoginUserDetailsServiceImpl;

public class LoginUserDetailsServiceImplTest {

    @Mock
    private AuthenticationMapper authenticationMapper;

    @InjectMocks
    private LoginUserDetailsServiceImpl loginUserDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* 要約
     * authenticationMapper.selectByUsername が存在するユーザーを返すときのテスト。
     * UserDetails が正しい情報を持ち、正しい権限を持っていることを確認します。
     * */
    @Test
    public void testLoadUserByUsername_UserExists() {
        // Arrange
        String username = "testuser";
        Authentication authentication = new Authentication();
        authentication.setUsername(username);
        authentication.setPassword("testpassword");
        authentication.setAuthority(Role.USER);

        when(authenticationMapper.selectByUsername(anyString())).thenReturn(authentication);

        // Act
        UserDetails userDetails = loginUserDetailsService.loadUserByUsername(username);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);

        // 型の不一致が起こり、テスト実行ができなかったので個別で作成。
        List<GrantedAuthority> expectedAuthorities = Collections.singletonList(new SimpleGrantedAuthority(Role.USER.name()));
        List<GrantedAuthority> actualAuthorities = userDetails.getAuthorities().stream().collect(Collectors.toList());
        assertThat(actualAuthorities).containsExactlyInAnyOrderElementsOf(expectedAuthorities);
    }

    /*　要約
     * authenticationMapper.selectByUsername が null を返すときのテスト。
     * UsernameNotFoundException がスローされることを確認します。
     * */
    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String username = "nonexistentuser";

        when(authenticationMapper.selectByUsername(anyString())).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException thrown = org.junit.jupiter.api.Assertions.assertThrows(
            UsernameNotFoundException.class,
            () -> loginUserDetailsService.loadUserByUsername(username)
        );
        assertThat(thrown.getMessage()).isEqualTo(username + " => 指定しているユーザー名は存在しません");
    }

    /*　要約
     * Role.USER に対して getAuthorityList メソッドをテスト。
     * User 権限だけがリストに含まれることを確認します。
     * */
    @Test
    public void testGetAuthorityList_WithUserRole() {
        // Arrange
        Role role = Role.USER;

        // Act
        List<GrantedAuthority> authorities = loginUserDetailsService.getAuthorityList(role);

        // Assert
        assertThat(authorities).containsExactly(new SimpleGrantedAuthority(Role.USER.name()));
    }

    /* 要約
     * Role.ADMIN に対して getAuthorityList メソッドをテスト。
     * Admin と User 権限の両方がリストに含まれることを確認します。
     * */
    @Test
    public void testGetAuthorityList_WithAdminRole() {
        // Arrange
        Role role = Role.ADMIN;

        // Act
        List<GrantedAuthority> authorities = loginUserDetailsService.getAuthorityList(role);

        // Assert
        List<GrantedAuthority> expectedAuthorities = new ArrayList<>();
        expectedAuthorities.add(new SimpleGrantedAuthority(Role.ADMIN.name()));
        expectedAuthorities.add(new SimpleGrantedAuthority(Role.USER.name()));
        assertThat(authorities).containsExactlyInAnyOrderElementsOf(expectedAuthorities);
    }
}
