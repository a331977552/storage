package com.storage.controller;

import com.storage.entity.Customer;
import com.storage.entity.custom.StorageResult;
import com.storage.remote.service.CustomerRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

	@Autowired
	CustomerRemoteService service;
	
	@RequestMapping("/user/update")
	public Object updateUser(@RequestBody Customer customer, HttpServletRequest request) {
		System.out.println(customer);
		StorageResult updateCustomer = service.updateCustomer(customer);
		if(updateCustomer.isSuccess()) {
			request.setAttribute("user", customer);
		}
		return updateCustomer;
	}
	
}
