package com.levels.ShiftSync.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.levels.ShiftSync.entity.Authentication;
import com.levels.ShiftSync.entity.Role;
import com.levels.ShiftSync.repository.AuthenticationMapper;

public class LoginUserDetailsServiceImplTest {

    @Mock
    private AuthenticationMapper authenticationMapper;

    @InjectMocks
    private LoginUserDatailsServiceImpl loginUserDatailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
        UserDetails userDetails = loginUserDatailsService.loadUserByUsername(username);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
//        assertThat(userDetails.getAuthorities())
//            .containsExactlyInAnyOrder(new SimpleGrantedAuthority(Role.USER.name()));
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String username = "nonexistentuser";

        when(authenticationMapper.selectByUsername(anyString())).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException thrown = org.junit.jupiter.api.Assertions.assertThrows(
            UsernameNotFoundException.class,
            () -> loginUserDatailsService.loadUserByUsername(username)
        );
        assertThat(thrown.getMessage()).isEqualTo(username + " => 指定しているユーザー名は存在しません");
    }
}
