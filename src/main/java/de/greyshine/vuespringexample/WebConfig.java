package de.greyshine.vuespringexample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
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