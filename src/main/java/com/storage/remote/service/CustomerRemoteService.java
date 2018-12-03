package com.storage.remote.service;

import com.storage.entity.Customer;
import com.storage.entity.custom.StorageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@FeignClient(value="back-service")
public interface CustomerRemoteService {


	@PostMapping("/customer/add")
	StorageResult<Customer>  addCustomer(@RequestBody Customer customer);

	@GetMapping("/customer/get/{id}")
	StorageResult<Customer> getCustomer(@PathVariable(name = "id") Integer id);

	@GetMapping("/customer/getbyName")
	StorageResult<List<Customer>> getCustomerByName(@RequestParam(name="name") String name);

	@GetMapping("/customer/getName/{order}.json")
	StorageResult<List<Customer>> getCustomer(@PathVariable(name="order")String order);

	@GetMapping("/customer/list")
	ResponseEntity<String> list();

	@GetMapping("/customer/delete/{id}")
	StorageResult<Customer>  deleteCustomerById(Integer id);

	@PostMapping("/customer/update")
	StorageResult  updateCustomer(@RequestBody Customer customer);

	@GetMapping("/customer/count")
	StorageResult<Long>  count();
	@PostMapping("/customer/getCustomerByExample")
	ResponseEntity<String> getCustomerByExample(@RequestBody Customer customer);
	
	@PostMapping("/customer/login")
	StorageResult<Customer> login(@RequestBody Customer customer);

	@GetMapping("/customer/createEmptyUser")
    ResponseEntity<Customer> createEmptyUser();
	@GetMapping("/customer/getbySession")
	ResponseEntity<Customer> getCustomerbySessionId(@RequestParam(name="sessionId")  String sessionId);
}