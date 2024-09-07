package com.levels.ShiftSync.error_page;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorPageTest {
	@Autowired
	private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;
    private User user; // ログインユーザー情報

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(SecurityMockMvcConfigurers.springSecurity())  // Spring Securityの設定を適用
                .build();
        
    	// 認証されたユーザーを設定
    	user = new User("1", "testpass", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
    	SecurityContextHolder.getContext().setAuthentication(
    			new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities())
    			);
    }
    
    
    @Test
    void test404ErrorPage() throws Exception {
        // 存在しないURLにアクセスして404エラーを確認
        mockMvc.perform(get("/non-existent-url"))
                .andExpect(status().isNotFound());  // 404 Not Found
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void test403ErrorPage() throws Exception {
        // 権限がないURLにアクセスして403エラーを確認
        mockMvc.perform(get("/attendance-requests-list"))  // このURLはADMIN権限が必要と仮定
                .andExpect(status().isForbidden());  // 403 Forbidden
    }
}
