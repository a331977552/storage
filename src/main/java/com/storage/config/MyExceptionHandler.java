package com.storage.config;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice()
public class MyExceptionHandler {

/*	@ExceptionHandler(SizeLimitExceededException.class)
	@ResponseBody
	public StorageResult handleException(SizeLimitExceededException e) {
		System.out.println("handleException");
		return StorageResult.failed("file is too large, size cannot exceed 5MB");
	}*/


}
