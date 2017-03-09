package com.pccw.hkt.cascade.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.pccw.hkt.cascade.Utils.ConstantConfig;
import com.pccw.hkt.cascade.Utils.ResponseCode;
import com.pccw.hkt.cascade.Utils.ResponseMessage;
import com.pccw.hkt.cascade.model.response.BaseResponse;
import com.pccw.hkt.cascade.model.response.LoginResponse;

public class SessionInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(ConstantConfig.SESSION_USER_KEY);
		if(obj != null){
			return true;
		}else{
			if(isAjaxRequest(request)){
				BaseResponse baseResponse = new LoginResponse(ResponseCode.ERR102, ResponseMessage.ERR102, false);
				String resultJson = new Gson().toJson(baseResponse);
				response.getWriter().write(resultJson);
			} else {
				response.sendRedirect("/");
			}
			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		
		
	}

	private boolean isAjaxRequest(HttpServletRequest request){
		String requestType = request.getHeader("X-Requested-With"); 
		if("XMLHttpRequest".equals(requestType)){
			return true;
		}
		return false;
	}
	
}
