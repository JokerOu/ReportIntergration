package com.pccw.hkt.cascade.dao;

import com.pccw.hkt.cascade.model.User;

public interface UserMapper {

	public User selectUserInfo(int staffNo);
	
	public void insertUser(User user);
	
	public void updateUser(User user);
	
	public void deleteUser(int staffNo);
	
}
