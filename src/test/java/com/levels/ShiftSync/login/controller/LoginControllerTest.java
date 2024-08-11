package com.levels.ShiftSync.login.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import com.levels.ShiftSync.controller.LoginController;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        }
        
    	@Bean
    	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    		http
    			// ★HTTPリクエストに対するセキュリティ設定
    			.authorizeHttpRequests(authz -> authz
    			// 「/login」へのアクセスは認証を必要としない
    			.requestMatchers("/login").permitAll()
    			// TODO:決裁者しかアクセスでいきない場合の実装で追加
    			//.requestMatchers("/attendance/admin").hasAuthority("ADMIN")
    			// その他のリクエストは認証が必要
    			.anyRequest().authenticated())
    			// ★フォームベースのログイン設定
    			.formLogin(form -> form
    			// カスタムログインページのURLを指定
    			.loginPage("/login")
    			// ログイン処理のURLを指定
    			.loginProcessingUrl("/authentication")
    			// ユーザー名のname属性を指定
    			.usernameParameter("usernameInput")
    			// パスワードのname属性を指定
    			.passwordParameter("passwordInput")
    			// ログイン成功時のリダイレクト先を指定
    			.defaultSuccessUrl("/")
    			// ログイン失敗時のリダイレクト先を指定
    			.failureUrl("/login?error"))
    			// ★ログアウト設定
    			.logout(logout -> logout
    			// ログアウトを処理するURLを指定
    			.logoutUrl("/logout")
    			// ログアウト成功時のリダイレクト先を指定
    			.logoutSuccessUrl("/login?logout")
    			// ログアウト時にセッションを無効にする
    			.invalidateHttpSession(true)
    			// ログアウト時にCookieを削除する
    			.deleteCookies("JSESSIONID")
    			);
    		return http.build();
    	}
    }

    @Test
    public void testShowLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
