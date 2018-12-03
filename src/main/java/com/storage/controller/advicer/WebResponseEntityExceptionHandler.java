package com.storage.controller.advicer;

import com.storage.component.RestResponseStatusExceptionResolver;
import com.storage.controller.FeedBackController;
import com.storage.controller.HomeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice(assignableTypes={HomeController.class,FeedBackController.class})
public class WebResponseEntityExceptionHandler {


    @Autowired
    RestResponseStatusExceptionResolver restResponseStatusExceptionResolver;
    @ExceptionHandler(value 
      = { Exception.class})
    protected ModelAndView handleConflict(HttpServletRequest request, HttpServletResponse response,Exception ex) {
        return restResponseStatusExceptionResolver.resolveException(request,response,null,ex);
    }
    }