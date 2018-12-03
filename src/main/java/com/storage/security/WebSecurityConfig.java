package com.storage.security;

import com.storage.entity.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity()
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter{


	//	@Autowired
	//	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
	//		auth.inMemoryAuthentication().withUser("bill").password("abc123").roles("USER");
	//		auth.inMemoryAuthentication().withUser("admin").password("root123").roles("ADMIN");
	//		auth.inMemoryAuthentication().withUser("dba").password("root123").roles("ADMIN","DBA");
	//	}

	@Autowired
	CustomerDetailService userdetailService;
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/static/**","/dist/**","/plugins/**");
	}

	@Autowired
	MyAuthenticationProvider authenticationprovider;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userdetailService);
		auth.authenticationProvider(this.authenticationprovider);

	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests().antMatchers("/**").
		permitAll().and().formLogin().loginPage("/login").loginProcessingUrl("/login").successHandler(new SimpleLoginSuccessHandler())
		.permitAll().and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/my_logout")
		.deleteCookies("JSESSIONID", Constants.USER_SESSION_ID)
		.invalidateHttpSession(true).and().rememberMe().tokenValiditySeconds(3600*24);
		
		
		
	}




}
