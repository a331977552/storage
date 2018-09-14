package com.storage.remote.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.storage.entity.Cart;
import com.storage.entity.custom.StorageResult;

@FeignClient("back-service")
public interface CartRemoteService {

	@RequestMapping("/api/cart/add")
	public StorageResult<Cart> addCart(@RequestBody Cart cart);
	@RequestMapping("/api/cart/empty/{id}")
	public StorageResult emptyCart(@PathVariable(name="id") Integer userId);
	@RequestMapping("/api/cart/merge")
	public StorageResult<Cart> merge(@RequestBody Cart cart);
	
	@RequestMapping("/api/cart/findByUserId/{id}")
	public StorageResult<Cart> findByUserId(@PathVariable(name="id")  Integer userId);
	
}
