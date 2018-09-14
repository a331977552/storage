package com.storage.remote.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.storage.entity.Category;
import com.storage.entity.custom.CustomeCategory;
import com.storage.entity.custom.StorageResult;
@FeignClient(value="back-service")

public interface CategoryRemoteService {

	@PostMapping("/category/add")
	StorageResult<Category> addCategory(Category category);
	@GetMapping("/category/get/{id}")
	StorageResult<Category> getCategory(@PathVariable(name="id") Integer id);
	@GetMapping("/category/delete/{id}")
	StorageResult<Category> deleteCategoryById(@PathVariable(name="id") Integer id);

	/*	@PostMapping("/update")
	public Object updateCategory(Category category) {
		return this.service.updateCategory(category);
	}*/
	@PostMapping("/category/update")
	StorageResult<Category> updateCategory(CustomeCategory category);
	@GetMapping("/category/count")
	StorageResult<Long> count();
	
	@GetMapping("/category/findAll")
	ResponseEntity<String> findAll();
	

}