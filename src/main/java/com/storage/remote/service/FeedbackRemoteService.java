package com.storage.remote.service;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.storage.entity.FeedBack;

@FeignClient("back-service")
public interface FeedbackRemoteService {


		@PutMapping("/feedback")
		public Optional<FeedBack> addFeedBack(@RequestBody FeedBack feedback);
	
		
	

	
}
