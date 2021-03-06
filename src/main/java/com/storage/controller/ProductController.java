package com.storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.storage.entity.Product;
import com.storage.entity.custom.PageBean;
import com.storage.entity.custom.StorageResult;
import com.storage.remote.service.ProductRemoteService;
import com.storage.remote.service.SettingRemoteService;
import com.storage.utils.JsonUtils;

@RestController()
@RequestMapping("/product")
public class ProductController {

	
	
	@Autowired
	ProductRemoteService productService;
	@Autowired
	SettingRemoteService settingRemoteService;
	
	@RequestMapping("/getProducts")
	public StorageResult<PageBean<Product>> getProducts(@RequestBody(required=false) Product product,@RequestParam(required=false,value="currentPage") Integer currentPage,@RequestParam(value="pageSize",required=false)Integer pageSize,
			@RequestParam(value="sort",required=false)String sort,@RequestParam(value="categoryId",required=false)Integer categoryId
			,@RequestParam(value="offerConfirmed",required=false)Integer offerConfirmed){
			
			
		
		ResponseEntity<String> json = productService.getProduct(product, currentPage, pageSize,sort,categoryId,offerConfirmed);
		
		if(json.getStatusCodeValue()==200 &&json.hasBody()) {
			String body = json.getBody();	
			StorageResult<PageBean<Product>> products = JsonUtils.jsonToObject(body, new TypeReference<StorageResult<PageBean<Product>>>() {
			});
		
			
			return products;	
		}
		
		return StorageResult.failed(json.getStatusCodeValue() + json.getBody());
		
		
	}
	@RequestMapping("/getName/{categoryId}.json")
	@ResponseBody
	public Object getProductNamesByCategory(@PathVariable(name = "categoryId") Integer categoryId) {
		ResponseEntity<String> result = this.productService.getProductNamesByCategory(categoryId);

		return result.getBody();
	}

	@RequestMapping("/getbestsellingproduct")
	@ResponseBody
	public Object getBestSellingProduct(@RequestParam("category")Integer categoryId) {
		ResponseEntity<String> result = this.productService.getBestSellingProduct(categoryId);

		return result.getBody();
	}
	
	
}
