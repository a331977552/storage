package com.storage.security;

import com.storage.entity.Customer;
import com.storage.entity.custom.AuthCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;


@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private CustomerDetailService userService;
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String name = authentication.getName();
		String password = (String) authentication.getCredentials();
		try {
			UserDetails userDetails = this.userService.loadUserByUsername(name);
			String password2 = userDetails.getPassword();
			if(!password2.equals(password)) {
				throw new BadCredentialsException("password is wrong");
			}

			Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
			Customer customer = ((AuthCustomer) userDetails).getCustomer();
			return new UsernamePasswordAuthenticationToken(customer, password,authorities);

		} catch (UsernameNotFoundException e) {
			//			e.printStackTrace();
			String message = e.getMessage();
			throw new BadCredentialsException(message);
		}


	}
	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

}
