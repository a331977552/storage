package com.storage.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.storage.entity.Customer;
import com.storage.entity.custom.OrderTableItem;
import com.storage.entity.custom.StorageResult;
import com.storage.remote.service.StOrderRemoteService;
import com.storage.utils.JsonUtils;

@RestController
public class OrderController {
	@Autowired
	StOrderRemoteService orderService;
	@RequestMapping("/orders/user")
	public Object getOrdersByUserId(HttpSession session) {
		Object attribute = session.getAttribute("user");
		if(attribute==null) {
			return StorageResult.failed("session expired!");
		}
		Customer customer=(Customer) attribute;
		 ResponseEntity<String> findAllOrderByUserId = orderService.findAllOrderByUserId(customer.getId());
		 StorageResult<List<OrderTableItem>> jsonToObject = JsonUtils.jsonToObject(findAllOrderByUserId.getBody(), new TypeReference<StorageResult<List<OrderTableItem>>>() {
		});

		 String objectToJson = JsonUtils.objectToJson(jsonToObject.getResult());
		 
		 return "{\"data\":"+objectToJson+"}";
		
	}
	
}
