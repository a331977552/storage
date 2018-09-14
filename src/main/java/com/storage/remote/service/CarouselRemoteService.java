package com.storage.remote.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.storage.entity.Carousel;
import com.storage.entity.custom.StorageResult;

@FeignClient("back-service")
public interface CarouselRemoteService {


		@PutMapping("/api/carousel")
		public StorageResult<Carousel> addCarousel(@RequestBody Carousel carousel);
		@PatchMapping()
		public StorageResult<Carousel> updateCarousel(@RequestBody Carousel carousel);
		@DeleteMapping("/api/carousel/{id}")
		public StorageResult<String> deleteCarousel(@PathVariable("id") Integer id);
		@GetMapping("/api/carousel/{id}")
		public StorageResult<Carousel> getCarousel(@PathVariable("id") Integer id);
		@GetMapping("/api/carousel/findAll")
		public ResponseEntity<String> getAllCarousel();
		@GetMapping("/api/carousel/findByExample")
		public StorageResult<List<Carousel>> getAllCarousel(@RequestBody Carousel  carousel);
		
		
	

	
}
