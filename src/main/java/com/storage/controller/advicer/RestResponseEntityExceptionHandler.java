package com.storage.controller.advicer;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.storage.controller.CartController;
import com.storage.controller.OrderController;
import com.storage.controller.ProductController;
import com.storage.controller.UserController;
import com.storage.entity.custom.StorageResult;

@ControllerAdvice(assignableTypes = { 
		CartController.class, 
		OrderController.class, 
		ProductController.class,
		UserController.class })
public class RestResponseEntityExceptionHandler {


    @ExceptionHandler(value 
      = { Exception.class})
    @ResponseBody
    protected Object handleConflict(
      Exception ex, WebRequest request) {
    	String message = ex.getMessage();
    	String bodyOfResponse;
    	if(message.startsWith("The temporary upload location")) {
    		bodyOfResponse = "服务器繁忙! code: 199";
    	}else
    	if(message.contains("Load balancer does not")) {
    		bodyOfResponse="服务器繁忙! code: 1100";
    	}else {
    		bodyOfResponse=message+"code: 1109";
    	}
    	if(ex instanceof feign.RetryableException) {
    		bodyOfResponse="服务器繁忙! code: 1110";
    	}
    	
        return StorageResult.failed(bodyOfResponse);
}
    }