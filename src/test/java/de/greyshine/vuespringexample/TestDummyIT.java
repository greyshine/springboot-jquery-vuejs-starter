package de.greyshine.vuespringexample;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import de.greyshine.vuespringexample.db.entity.User;
import de.greyshine.vuespringexample.db.repos.UserRepository;
import de.greyshine.vuespringexample.services.LoginService;

@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = WebEnvironment.NONE )
public class TestDummyIT {
	
	public static final Logger LOG = LoggerFactory.getLogger( TestDummyIT.class );
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LoginService loginService;
	
	@Test
	public void createUser() {
		
		loginService.create( "Login1", "password", "test@somewhere.de", true);
		userRepository.findAll().forEach( u -> LOG.info("{}", u) );
	}
	
	@Test
	public void showUsers() {
		userRepository.findAll().forEach( u -> LOG.info("{}", u) );
	}
	
}
