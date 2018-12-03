package com.storage.component;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class RestResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver {


    @Override
    protected ModelAndView doResolveException
            (HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String message = ex.getMessage();
        String bodyOfResponse;
        if(message.startsWith("The temporary upload location")) {
            bodyOfResponse = "服务器繁忙! code: 99";
        }else
        if(message.contains("Load balancer does not")) {
            bodyOfResponse="服务器繁忙! code: 100  load balancer gone";
        }else {
            bodyOfResponse=message+"code: 109";
        }
        if(ex instanceof feign.RetryableException) {
            bodyOfResponse="服务器繁忙! code: 110, load balancer gone";
        }
        ex.printStackTrace();
        ModelAndView andView=new ModelAndView();
        andView.addObject("error",bodyOfResponse);
        andView.setViewName("redirect:/error");
        return andView;
    }




}
