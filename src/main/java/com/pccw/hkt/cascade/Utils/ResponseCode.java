package com.pccw.hkt.cascade.Utils;

public enum ResponseCode {
	
	
	/*
	 * 1xx : login errors
	 */
	ACC200,//SUCCESS
	ERR999,//Unknown error, please try again later
	
	ERR101,//Invalid user name or password
	ERR102,//Have not logined or session time
	
}
