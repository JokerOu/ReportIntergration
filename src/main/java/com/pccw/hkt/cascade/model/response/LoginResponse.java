package com.pccw.hkt.cascade.model.response;

import com.pccw.hkt.cascade.Utils.ResponseCode;


public class LoginResponse extends BaseResponse{

	public LoginResponse(){
		super();
	}
	
	public LoginResponse(ResponseCode ec, String msg, boolean success){
		this.setResponseCode(ec);
		this.message = msg;
		this.success = success;
	}
	
}
