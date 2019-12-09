package de.greyshine.vuespringexample;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import de.greyshine.vuespringexample.db.entity.User;
import de.greyshine.vuespringexample.services.UserService;

@Configuration
public class LocalDevelopmentSetup {
	
	private static final Logger LOG = LoggerFactory.getLogger(LocalDevelopmentSetup.class);
	
	@Value("${environment}")
    private String environment;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 
	 * for activating environment 'development' start with
	 * -Denvironment=development
	 */
	@PostConstruct
	public void postConstruct() {

		if ( !"development".equalsIgnoreCase( environment ) ) { return; }
		
		if ( !userService.isLogin( "testuser1" ) ) {
			final User user = userService.create("testuser1", "pwd", "kuemmel.dss@gmx.de", new String[] { "testuser" }, false);
			LOG.debug("creating user: {}", user);
		}
		
		userService.setActiveState("testuser1", true);
	}

}
