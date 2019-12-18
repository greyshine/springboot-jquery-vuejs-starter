package de.greyshine.vuespringexample;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import de.greyshine.vuespringexample.db.entity.User;
import de.greyshine.vuespringexample.services.ContractAgreementService;
import de.greyshine.vuespringexample.services.UserService;
import de.greyshine.vuespringexample.utils.Utils;

@Configuration
public class LocalDevelopmentSetup {
	
	private static final Logger LOG = LoggerFactory.getLogger(LocalDevelopmentSetup.class);
	
	@Value("${environment:}")
    private String environment;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ContractAgreementService contractAgreementService;
	
	/**
	 * 
	 * for activating environment 'development' start with
	 * -Denvironment=development
	 */
	@PostConstruct
	public void postConstruct() {
		
		environment = Utils.trimToNull( environment );

		if ( !"development".equalsIgnoreCase( environment ) ) { return; }
		
		if ( !userService.isLogin( "testuser1" ) ) {
			final User user = userService.create("testuser1", "pwd", "kuemmel.dss@gmx.de", new String[] { "testuser" }, false);
			LOG.debug("creating user: {}", user);
		}
		
		userService.setActiveState("admin", true);
		userService.setActiveState("testuser1", true);
		
		
		LocalDateTime validFrom = LocalDateTime.now().minusDays(1);
		String context = "context-1";
		String text = "Some TOC text\nMore text lines.\neven more lines...";
		contractAgreementService.save(validFrom, context, text);
		
		validFrom = LocalDateTime.now().minusDays(2);
		context = "context-2";
		text = "Some OTHER text";
		contractAgreementService.save(validFrom, context, text);
	}

}
