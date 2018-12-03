package com.storage.intercepter;

import com.storage.security.MyAuthenticationProvider;
import com.storage.entity.Customer;
import com.storage.entity.utils.Constants;
import com.storage.remote.service.CustomerRemoteService;
import com.storage.utils.CookieUtils;
import com.storage.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;


@Component
public class LoginIntercepter extends  HandlerInterceptorAdapter{

	@Autowired
	CustomerRemoteService service;
	@Autowired
	MyAuthenticationProvider authManager;
@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null &&authentication.getPrincipal() instanceof Customer)
		{
				Customer manager=(Customer) authentication.getPrincipal();
				request.setAttribute(Constants.USER, manager);

		}else{
			String cookieValue = CookieUtils.getCookieValue(request,Constants.USER_SESSION_ID);
			if(StringUtils.isEmpty(cookieValue))
			{
				ResponseEntity<Customer> emptyUser = service.createEmptyUser();
				String sessionId = Objects.requireNonNull(emptyUser.getBody()).getSessionId();
				CookieUtils.setCookie(request,response,Constants.USER_SESSION_ID,sessionId,Constants.USER_COOKIETIME );
			}else{
				ResponseEntity<Customer> customerByExample = service.getCustomerbySessionId(cookieValue.trim());
				Customer customer = Objects.requireNonNull(customerByExample.getBody());
				Integer isEmpty = customer.getIsEmpty();
				if(isEmpty==Constants.USER_FILLED_STATE){
					UsernamePasswordAuthenticationToken authReq
							= new UsernamePasswordAuthenticationToken(customer.getEmail(), customer.getPassword());
					Authentication auth = null;
					try {
						auth = authManager.authenticate(authReq);
					} catch (AuthenticationException e) {
						e.printStackTrace();
						System.out.println("FIX ME -- LoginIntercepter 111");
					}

					SecurityContext sc = SecurityContextHolder.getContext();
					sc.setAuthentication(auth);
					HttpSession session = request.getSession(true);
					session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
					customer.setUsername("");
					customer.setPassword("");
					request.setAttribute(Constants.USER,customer);
				}else{
					//do nothing
				}

			}
		}


		return true;
	}
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	if(ex instanceof NullPointerException){
		response.sendRedirect("error");
	}
}

}
