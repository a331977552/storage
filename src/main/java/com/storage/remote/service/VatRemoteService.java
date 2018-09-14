package com.storage.remote.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.storage.entity.StOrder;
import com.storage.entity.Vat;
import com.storage.entity.custom.StorageResult;

@FeignClient(value="back-service")
public interface VatRemoteService {


	@PostMapping("/vat/add")
	StorageResult<Vat>   addVat(Vat vat);
	@GetMapping("/vat/get/{id}")
	StorageResult<Vat>   getVat(@PathVariable(name = "id") Integer id);
	@GetMapping("/delete/{id}")
	StorageResult<Vat>   deleteVatById(@PathVariable(name = "id") Integer id);

	@PostMapping("/vat/update")
	StorageResult<Vat>   updateVat(Vat vat);
	@GetMapping("/vat/count")
	StorageResult<Long>   count();
	@GetMapping("/vat/findAll")
	ResponseEntity<String>   findAll();

}