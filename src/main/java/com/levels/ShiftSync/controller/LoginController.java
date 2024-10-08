package com.levels.ShiftSync.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.levels.ShiftSync.form.LoginForm;

@Controller
@RequestMapping("/login")
public class LoginController {
	// ログイン画面を表示する
	@GetMapping
	public String showLogin(@ModelAttribute LoginForm form) {
		// templatesフォルダ配下のlogin.htmlに遷移
		return "login";
	}
}
