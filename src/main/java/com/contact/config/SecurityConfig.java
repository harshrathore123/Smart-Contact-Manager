	package com.contact.config;
	
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
	import org.springframework.security.config.annotation.web.builders.HttpSecurity;
	import org.springframework.security.core.userdetails.UserDetailsService;
	import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
	import org.springframework.security.web.SecurityFilterChain;
	
	@Configuration
	public class SecurityConfig {
	
		private final UserDetailsService userDetailService;
	
		public SecurityConfig(UserDetailsService userDetailService) {
			super();
			this.userDetailService = userDetailService;
		}
	
		// Password Encryption
		@Bean
		public BCryptPasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
	
		// Authentication Provider
		@Bean
		public DaoAuthenticationProvider authenticationProvider() {
			DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailService);
			provider.setPasswordEncoder(passwordEncoder());
	
			return provider;
		}
	
		// Security Configuration
		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	
		    http.authorizeHttpRequests(auth -> auth
	
		            .requestMatchers("/admin/**").hasRole("ADMIN")
		            .requestMatchers("/user/**").hasRole("USER")
		            .requestMatchers("/customer/**").hasRole("CUSTOMER")
	
		            .requestMatchers("/**")
		            .permitAll()
		    )
	
		    .formLogin(form -> form
		    		.loginPage("/signin")
		    		.loginProcessingUrl("/doLogin")
		    		.defaultSuccessUrl("/customer/data")
		    		.failureUrl("/login_failure")
		            .permitAll()
		    )
	
		    .logout(logout -> logout
		    		.logoutRequestMatcher(request ->
		    		"/logout".equals(request.getServletPath())
		    		&& "GET".equals(request.getMethod())
		    		)
		    		.logoutSuccessUrl("/signin?logout")
		            .permitAll()
		    );
	
		    return http.build();
		}
	
	}
