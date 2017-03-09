package com.pccw.hkt.cascade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pccw.hkt.cascade.model.Team;
import com.pccw.hkt.cascade.model.User;
import com.pccw.hkt.cascade.service.UserService;

@Controller
@SpringBootApplication
public class TestController {

	@Autowired
	private UserService userService;
	
	@ResponseBody
	@RequestMapping(value="/user/select")
	public User selectUser(){
		try {
			return userService.selectUser(80575129);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/team/select")
	public List<Team> selectTeam(){
		try {
			return userService.getAllTeams();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/team/users")
	public List<User> selectTeamUsers(){
		List<User> users = null;
		try {
			List<Team> teams = userService.getAllTeams();
			if(teams!=null && teams.size() > 0){
				users = userService.getTeamUsers(teams.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}
	
	@ResponseBody
	@RequestMapping(value="/users/{username}")
	public String user(@PathVariable("username") String username){
		return String.format("%s", username);
	}
	
	
	
}
