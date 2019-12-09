package de.greyshine.vuespringexample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//WARN: enabling @EnableWebMvc
// if enabled then web resources are not served from /resources/public/*
public class WebConfig implements WebMvcConfigurer {
	
	/**
	 * Sets up the interceptor with e.g. @Autowired components
	 * 
	 * @return spring setup {@link AccessInterceptor}
	 */
	@Bean
	public AccessInterceptor accessInterceptor() {
		return new AccessInterceptor();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor( accessInterceptor() );
	}

}