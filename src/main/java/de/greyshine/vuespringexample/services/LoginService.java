package de.greyshine.vuespringexample.services;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.greyshine.vuespringexample.db.entity.User;
import de.greyshine.vuespringexample.db.repos.UserRepository;

@Service
public class LoginService {
	
	private static final Logger LOG = LoggerFactory.getLogger( LoginService.class );
	
	@Autowired
	private UserRepository userRepository;

	public boolean login(String login, String password) {
		
		if ( "admin".equals( login ) ) { return true; }
		
		return false;
	}
	
	public void create(String login, String password, String email, boolean active) {
		
		Assert.hasText( login, "login is blank" );
		Assert.hasText( password, "password is blank" );
		Assert.hasText( email, "email is blank" );
		
		User user = new User();
		user.setLogin( login.strip() );
		user.setPassword( passwordSha512( password ) );
		user.setActive( active );
		user.setEmail( email.strip() );
		
		user = userRepository.save( user );
		
		LOG.debug("created: {}", user);
	}

	private String passwordSha512(String password) {
		
		if ( password == null || password.strip().isBlank() ) { return null; }
		
		try {
		
			final MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update( password.getBytes("utf8") );
			return String.format("%0128x", new BigInteger(1, digest.digest()));

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new IllegalStateException( e );
		}
	}
	
	
	
	

}
