package com.storage.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SimpleLoginSuccessHandler implements AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess( HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		HttpSession session = request.getSession();
//		MyManager manager=(MyManager) authentication.getPrincipal();
//		session.setAttribute("user",manager);
		session.setMaxInactiveInterval(36000);
		response.sendRedirect(request.getContextPath()+"/index");

		//		TokenBasedRememberMeServices
		/*if( manager.isRemember()) {
			Manager r=(Manager) result.getResult();
			CookieUtils.setCookie(request, response, this.tokenName,r.getToken() );
		}*/


	}



}
