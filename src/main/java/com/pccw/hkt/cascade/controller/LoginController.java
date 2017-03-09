package com.pccw.hkt.cascade.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pccw.hkt.cascade.Utils.ResponseCode;
import com.pccw.hkt.cascade.model.response.LoginResponse;
import com.pccw.hkt.cascade.service.UserService;

@Controller
public class LoginController {

	@Autowired
	private UserService userServcie;
	
	@RequestMapping("/")
	public String index(){
		return "login/index";
	}
	
	@RequestMapping("/login")
	public String login(String staffNo, String password, Model model, HttpSession session){
		LoginResponse res = userServcie.login(staffNo, password, session);
		model.addAttribute("response", res);
		
		if(res.getResponseCode().equals(ResponseCode.ACC200)){
			return "main/index";
		} else {
			return "error";
		}
	}
	
}
