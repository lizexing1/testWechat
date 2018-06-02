package com.lizx.user.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
public class UserController {

	@RequestMapping("list")
	public void list(HttpServletRequest request) {
		String openid = (String) request.getSession().getAttribute("openid");
		System.out.println("===openid====openid==="+openid);
	}
	
}
