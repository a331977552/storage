package com.storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.storage.entity.FeedBack;
import com.storage.entity.Setting;
import com.storage.entity.custom.StorageResult;
import com.storage.remote.service.FeedbackRemoteService;
import com.storage.remote.service.SettingRemoteService;

@Controller
public class FeedBackController {

	@Autowired
	FeedbackRemoteService service;
	
	@Autowired
	SettingRemoteService settingService;
	@PostMapping("/feedback_submit")
	public ModelAndView feedback(FeedBack back,ModelAndView view) {
		view.setViewName("redirect:/index");
		if(!(back==null || com.storage.utils.StringUtils.isEmpty(back.getComment()))) {
			service.addFeedBack(back);			
		}		
		return view;

	}

	@GetMapping("/contact")
	public ModelAndView contact(FeedBack back,ModelAndView view) {
		
		service.addFeedBack(back);
		StorageResult<Setting> setting = settingService.getSetting();
		view.addObject("setting",setting.getResult());
		view.setViewName("feedback");
		
		return view;
		
	}
	
}
