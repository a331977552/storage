package com.storage;

import com.storage.intercepter.LoginIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class FrontApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(FrontApplication.class, args);
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		WebMvcConfigurer.super.addViewControllers(registry);
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/register").setViewName("register");
			
//		HttpServletResponse
	}
	@Autowired
	LoginIntercepter loginIntercepter;
@Override
public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(localeChangeInterceptor());
	registry.addInterceptor(loginIntercepter).excludePathPatterns("/error/**","/error-404/**","/error-500/**","/error-403/**",
			"/static/**","/dist/**","/plugins/**"
			);


}

	@Bean
	public LocaleResolver localeResolver() {
	    SessionLocaleResolver slr = new SessionLocaleResolver();
	    slr.setLocaleAttributeName("local_lang_my");
	    slr.setDefaultLocale(Locale.CHINA);
	    return slr;
	}
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
	    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
	    lci.setParamName("lang");
	    return lci;
	}
	
}
