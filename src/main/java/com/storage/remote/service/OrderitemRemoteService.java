package com.storage.remote.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.storage.entity.Orderitem;
import com.storage.entity.custom.StorageResult;

@FeignClient(value="back-service")
public interface OrderitemRemoteService {


	@PostMapping("/orderitem/add")
	StorageResult<Orderitem> addOrderitem(Orderitem orderitem);
	@GetMapping("/orderitem/get/{id}")
	StorageResult<Orderitem> getOrderitem(@PathVariable(name = "id")Integer id);

	@GetMapping("/orderitem/delete/{id}")
	StorageResult<Orderitem> deleteOrderitemById(@PathVariable(name = "id") Integer id);

	@PostMapping("/orderitem/update")
	StorageResult<Orderitem> updateOrderitem(Orderitem orderitem);

	@GetMapping("/orderitem/count")
	StorageResult<Long> count();

}