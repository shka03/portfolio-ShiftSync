package com.levels.ShiftSync.login.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AttendanceRecordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testShowAttendanceWithAuthenticatedUser() throws Exception {
        // 認証されたユーザーを設定
        User user = new User("testuser", "testpass", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities())
        );

        // テスト実行
        mockMvc.perform(get("/").with(SecurityMockMvcRequestPostProcessors.user(user)))
               .andExpect(status().isOk())
               .andExpect(view().name("attendance"));
    }
}
