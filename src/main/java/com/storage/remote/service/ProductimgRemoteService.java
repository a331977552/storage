package com.storage.remote.service;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.storage.entity.Orderitem;
import com.storage.entity.Productimg;
import com.storage.entity.custom.StorageResult;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

@FeignClient(value="back-service",configuration = {ProductimgRemoteService.MultipartSupportConfig.class})
public interface ProductimgRemoteService {
	@RequestMapping(value = {"/productimg/add"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = {RequestMethod.POST})
	StorageResult<Productimg>  addProductimg(@RequestPart(value = "file", required = true)  MultipartFile file);
	@GetMapping("/productimg/get/{id}")
	StorageResult<Productimg>  getProductimg(@PathVariable(name = "id") Integer id);
	@GetMapping("/productimg/delete/{id}")
	StorageResult<Productimg>  deleteProductimgById(@PathVariable(name = "id") Integer id);

	@PostMapping("/productimg/update")
	StorageResult<Productimg>  updateProductimg(Productimg productimg);
	@GetMapping("/productimg/count")
	StorageResult<Long>  count();
	
	  @Configuration
	    public class MultipartSupportConfig {
	 
	        @Autowired
	        private ObjectFactory<HttpMessageConverters> messageConverters;
	 
	        @Bean
	        @Primary
	        @Scope("prototype")
	        public Encoder feignEncoder() {
	            return new SpringFormEncoder(new SpringEncoder(messageConverters));
	        }
	    }

	
	
}