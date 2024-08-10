package com.levels.ShiftSync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.
EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	// SecurityFilterChainのBean定義
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// ★HTTPリクエストに対するセキュリティ設定
			.authorizeHttpRequests(authz -> authz
			// 「/login」へのアクセスは認証を必要としない
			.requestMatchers("/login").permitAll()
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
			.failureUrl("/login?error")
			);
		return http.build();
	}
}
