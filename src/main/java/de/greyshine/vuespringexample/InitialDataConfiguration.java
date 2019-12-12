package de.greyshine.vuespringexample;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import de.greyshine.vuespringexample.services.UserService;
import de.greyshine.vuespringexample.web.GeolocationDbComLocator;
import de.greyshine.vuespringexample.web.IpLocator;

@Configuration
public class InitialDataConfiguration {
	
	private static final Logger LOG = LoggerFactory.getLogger( InitialDataConfiguration.class );
	
	@SuppressWarnings("unused")
	@Autowired
    private Environment environment;
	
	@Autowired
	private UserService userService;
	
	@PostConstruct
	@Transactional
    public void postConstruct() {
		
		userService.ensureAdminUser();
		
		LOG.debug("Started after Spring boot application !");
    }
	
	@Bean
	public IpLocator ipLocator() {
		return new GeolocationDbComLocator();
	}

}
