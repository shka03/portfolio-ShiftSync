package com.levels.ShiftSync.utility;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
* ハッシュ化した文字列を返すクラス
* TODO:本機能を利用して、アカウント登録時に入力したPWをハッシュ化してテーブルに反映させる。
*/
public class PasswordGenerator {
 public static void main(String[] args) {
		// 「BCrypt」のインスタンス化
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 入力値
		String rawPassword = "adminpass";
		// パスワードをハッシュ化
		String encodedPassword = encoder.encode(rawPassword);
		// 表示
		System.out.println("ハッシュ化されたパスワード: " + encodedPassword);
	}
}
