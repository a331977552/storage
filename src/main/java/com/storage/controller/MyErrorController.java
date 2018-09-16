package com.storage.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
/**
 * TODO adding this into template
 * @author cody
 *
 */
@Controller
public class MyErrorController  implements ErrorController{

	@Override
	public String getErrorPath() {
		return "error";
	}
	@RequestMapping("/error")
	public ModelAndView handleError(HttpServletRequest request,ModelAndView model) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());

			if(statusCode == HttpStatus.NOT_FOUND.value()) {
				model.setViewName("error-404");
			}

			if(statusCode == HttpStatus.FORBIDDEN.value()) {
				model.setViewName("error-403");
			}
			else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				model.setViewName("error-500");
			}
		}else {
			model.setViewName("error");
		}
		Object attribute = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		model.addObject("msg",attribute);
		return model;
	}


}
