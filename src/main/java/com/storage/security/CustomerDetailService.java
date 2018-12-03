package com.storage.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.storage.entity.Customer;
import com.storage.entity.custom.AuthCustomer;
import com.storage.entity.custom.StorageResult;
import com.storage.remote.service.CustomerRemoteService;
import com.storage.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerDetailService implements UserDetailsService {

	@Autowired
	CustomerRemoteService service;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if(StringUtils.isEmpty(username.trim())) {
			throw new UsernameNotFoundException("null username");
		}
		Customer customer=new Customer();


		customer.setEmail(username);
		ResponseEntity<String> managerByExample = this.service.getCustomerByExample(customer);
		if(managerByExample.getStatusCodeValue()!=200)
			throw new UsernameNotFoundException("server busy");
		StorageResult<List<Customer>> jsonToObject = JsonUtils.jsonToObject(managerByExample.getBody(),new TypeReference<StorageResult<List<Customer>>>() {
		});
		if(!jsonToObject.isSuccess())
			throw new UsernameNotFoundException("2 can't find the email: "+ username);
		
		if(jsonToObject.getResult()!=null &&! jsonToObject.getResult().isEmpty()) {
			List<Customer> result = jsonToObject.getResult();
		
			Customer manager2 = result.get(0);
			List<SimpleGrantedAuthority> authorities=new ArrayList<>();
			SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");

			authorities.add(simpleGrantedAuthority);
			AuthCustomer manager3=new AuthCustomer(manager2.getEmail(),manager2.getPassword(), authorities);

			manager3.setCustomer(manager2);
			return manager3;
		}
		throw new UsernameNotFoundException("username doesn't match password ");
	}

}
