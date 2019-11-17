package de.greyshine.vuespringexample;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import de.greyshine.vuespringexample.services.LoginService;

@Configuration
public class InitialDataConfiguration {
	
	private static final Logger LOG = LoggerFactory.getLogger( InitialDataConfiguration.class );
	
	@Autowired
	private LoginService loginService;
	
	@PostConstruct
	@Transactional
    public void postConstruct() {
		
		loginService.ensureAdminUser();
		
		System.out.println("Started after Spring boot application !");
    }

	

}