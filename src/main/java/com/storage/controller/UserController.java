package com.storage.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storage.entity.Customer;
import com.storage.entity.custom.StorageResult;
import com.storage.remote.service.CustomerRemoteService;

@RestController
public class UserController {

	@Autowired
	CustomerRemoteService service;
	
	@RequestMapping("/user/update")
	public Object updateUser(@RequestBody Customer customer,HttpSession httpSession) {
		StorageResult updateCustomer = service.updateCustomer(customer);
		if(updateCustomer.isSuccess()) {
			httpSession.setAttribute("user", customer);
		}
		return updateCustomer;
	}
	
}
