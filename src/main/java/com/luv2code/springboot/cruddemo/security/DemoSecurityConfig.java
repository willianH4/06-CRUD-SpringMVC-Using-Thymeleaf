package com.luv2code.springboot.cruddemo.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class DemoSecurityConfig {
	
	// add suppor for JDBC ... no more hardcoded users :)
	
	@Bean
	public UserDetailsManager uDetailsManager(DataSource dataSource) {
		
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
		
		// define query retrieve a user by username
		jdbcUserDetailsManager.setUsersByUsernameQuery(
				"select user_id, pw, active from members where user_id=?"
				);
		
		// define query to retrieve the authorities/roles by username
		jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
				"select user_id, role from roles where user_id=?"
				);
		
		return jdbcUserDetailsManager;
		
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
