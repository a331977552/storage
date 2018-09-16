package com.storage.remote.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.storage.entity.Manager;
import com.storage.entity.custom.StorageResult;

@FeignClient(value="back-service")
public interface ManagerRemoteService {
	@PostMapping("/manager/add")
	StorageResult<Manager>  addManager(@RequestParam("manager")Manager manager);
	/*@PostMapping("/login")
	public ModelAndView loginManager(@AuthenticationPrincipal User user,  ModelAndView model,HttpServletRequest request,HttpServletResponse response, Manager manager) {
		System.out.println("1111111111111111111111");
	
		return model;
	}*/

	@GetMapping("/manager/get/{id}")
	StorageResult<Manager>   getManager(@PathVariable(name = "id") Integer id);
	@GetMapping("/manager/getOne")
	StorageResult<Manager>   getOne();

	@GetMapping("/manager/delete/{id}")
	StorageResult<Manager>   deleteManagerById(@PathVariable(name = "id") Integer id);
	/*@GetMapping("/logout")
	public Object signout() {
		return this.service.deleteManagerById(id);
	}*/

	@PostMapping("/manager/update")
	StorageResult<Manager>   updateManager(Manager manager);

	@GetMapping("/manager/count")
	StorageResult<Long>   count();
	@RequestMapping("/manager/getByExample")
	StorageResult<List<Manager>>   getManagerByExample( Manager manager);

}