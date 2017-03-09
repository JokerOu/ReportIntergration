package com.pccw.hkt.cascade.model.response;

import com.pccw.hkt.cascade.Utils.ResponseCode;

public class BaseResponse {

	private ResponseCode responseCode;
	protected String message;
	protected boolean success;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public ResponseCode getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}
	
}
