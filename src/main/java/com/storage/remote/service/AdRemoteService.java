package com.storage.remote.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("back-service")
public interface AdRemoteService {


	
		@GetMapping("/ad/getall")
		public ResponseEntity<String> getAllCarousel();
		
		
	

	
}
