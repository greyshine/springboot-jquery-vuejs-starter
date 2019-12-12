package de.greyshine.vuespringexample;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Configuration;


@Configuration
public class PreShutdownHooks {
	
	@PreDestroy
	public void preDestroy() {
		
	}

}
