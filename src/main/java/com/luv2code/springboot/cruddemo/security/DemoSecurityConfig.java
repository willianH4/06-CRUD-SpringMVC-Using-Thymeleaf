package com.luv2code.springboot.cruddemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class DemoSecurityConfig {

	@Bean
	public InMemoryUserDetailsManager userDetailsManager() {
		
		UserDetails john = User.builder()
				.username("john")
				.password("{noop}test123")
				.roles("EMPLOYEE")
				.build();
		
		UserDetails maria = User.builder()
				.username("maria")
				.password("{noop}test123")
				.roles("EMPLOYEE","ADMIN")
				.build();
		
		UserDetails susan = User.builder()
				.username("susan")
				.password("{noop}test123")
				.roles("EMPLOYEE","MANAGER","ADMIN")
				.build();
		
		return new InMemoryUserDetailsManager(john, maria, susan);
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity.authorizeHttpRequests(configurer ->
			configurer
				.requestMatchers("/").hasRole("EMPLOYEE")
				.requestMatchers("/employees/list").hasRole("MANAGER")
				.requestMatchers("/employees/**").hasRole("ADMIN")
				.anyRequest().authenticated()
		)
		.formLogin(form -> 
			form
				.loginPage("/showCustomLoginPage")
				.loginProcessingUrl("/authenticateTheUser")
				.permitAll()
			)
		
		.logout(logout -> logout.permitAll()
			)
		.exceptionHandling(configurer -> 
				configurer.accessDeniedPage("/access-denied")
		);
		
		return httpSecurity.build();
		
	}
	
}
