package com.storage.remote.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.storage.entity.Setting;
import com.storage.entity.custom.StorageResult;
@FeignClient(value="back-service")
public interface SettingRemoteService {
	@PostMapping("/setting/add")
	StorageResult<Setting> addSetting(Setting setting);
	@GetMapping("/setting/get/{id}")
	StorageResult<Setting>  getSetting(@PathVariable(name = "id")Integer id);
	@GetMapping("/setting/delete/{id}")
	StorageResult<Setting>  deleteSettingById(@PathVariable(name = "id")Integer id);
	@PostMapping("/setting/update")
	StorageResult<Setting>  updateSetting(Setting setting);
	@GetMapping("/setting/count")
	StorageResult<Long>  count();

}