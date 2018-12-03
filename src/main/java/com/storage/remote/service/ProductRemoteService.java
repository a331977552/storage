package com.storage.remote.service;


import com.storage.entity.Product;
import com.storage.entity.custom.CustomProduct;
import com.storage.entity.custom.StorageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(value="back-service")
public interface ProductRemoteService {

	@RequestMapping(value="/product/add",consumes= {MediaType.APPLICATION_JSON_UTF8_VALUE},produces= {MediaType.APPLICATION_JSON_UTF8_VALUE})
	StorageResult<Product>  addProductWithImg(@RequestBody() CustomProduct product);
	@GetMapping("/product/get/{id}")
	ResponseEntity<String>  getProduct(@PathVariable("id")Integer id);
	@RequestMapping("/product/barcode/get")
	ResponseEntity<String>   getProductByBarcode(@RequestParam(required=false,name="barcode",value="barcode") String barcode,@RequestParam(required=false,name="id",value="id") Integer id);
	@RequestMapping(value="/product/list", consumes= {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces= {MediaType.APPLICATION_JSON_UTF8_VALUE})

	ResponseEntity<String>   getProduct(
			@RequestBody(required=false) Product product, @RequestParam(value="currentPage",required=false)Integer currentPage, 
			@RequestParam(value="pageSize",required=false) Integer pageSize,
			@RequestParam(value="sort",required=false)String sort,@RequestParam(value="categoryId",required=false)Integer categoryId
			,@RequestParam(value="offerConfirmed",required=false)Integer offerConfirmed
			);
	@DeleteMapping("/product/delete/{id}")
	StorageResult<Product>   deleteProductById(@PathVariable("id")Integer id);
	@GetMapping("/product/stockreminder")
	StorageResult<List<Product>>  getStockReminder();
	@PostMapping("/product/update")
	StorageResult<Product> updateProduct(CustomProduct product);
	@RequestMapping(value="/product/getName/{categoryId}.json",consumes= {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces= {MediaType.APPLICATION_JSON_UTF8_VALUE})
	/*StorageResult<List<CustomeProductName>>*/
	
	ResponseEntity<String> getProductNamesByCategory(@PathVariable("categoryId") Integer categoryId);
	@GetMapping("/product/count")
	StorageResult<Long> count(@RequestBody Product product);
	@RequestMapping("/product/getProductByExample")
//	StorageResult<List<CustomeProductName>> 
	ResponseEntity<String> getProductByExample(@RequestBody String jsonToList);
	@RequestMapping("/product/getbestsellingproduct")	
	ResponseEntity<String> getBestSellingProduct(@RequestParam("category") Integer categoryId);
	@PostMapping("/product/getListByIds")
	 ResponseEntity<String> getProductsByIds(@RequestBody  List<Integer> ids);
}