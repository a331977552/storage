package com.storage.controller.advicer;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {


    @ExceptionHandler(value 
      = { Exception.class})
    
    protected ModelAndView handleConflict(
      Exception ex, WebRequest request) {
    	String message = ex.getMessage();
    	String bodyOfResponse;
    	if(message.startsWith("The temporary upload location")) {
    		bodyOfResponse = "服务器异常! code: 99";
    	}else
    	if(message.contains("Load balancer does not")) {
    		bodyOfResponse="服务器异常! code: 100";
    	}else {
    		bodyOfResponse=message+"code: 109";
    	}
    	if(ex instanceof feign.RetryableException) {
    		bodyOfResponse="服务器异常! code: 110";
    	}
    	ModelAndView andView=new ModelAndView();
    	andView.addObject("error",bodyOfResponse);
    	andView.setViewName("redirect:/error");
        return andView;
}
    }