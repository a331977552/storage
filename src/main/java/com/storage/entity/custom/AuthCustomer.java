package com.storage.entity.custom;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.storage.entity.Customer;

public class AuthCustomer  extends User {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AuthCustomer(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	private Customer customer;
	private List<SimpleGrantedAuthority> authorites;
	public List<SimpleGrantedAuthority> getAuthorites() {
		return authorites;
	}
	
	
	public void setAuthorites(List<SimpleGrantedAuthority> authorites) {
		this.authorites = authorites;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	

}
