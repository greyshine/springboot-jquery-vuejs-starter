package de.greyshine.vuespringexample.services;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.greyshine.vuespringexample.db.entity.User;
import de.greyshine.vuespringexample.db.repos.UserRepository;
import de.greyshine.vuespringexample.utils.Utils;

@Service
public class LoginService {
	
	private static final Logger LOG = LoggerFactory.getLogger( LoginService.class );

	public static final Logger LOG_USERLOGIN = LoggerFactory.getLogger( "UserLogin" );
	
	public static final String DEFAULT_ADMIN_LOGIN = "admin";
	/**
	 * Change that after first start of app!
	 */
	public static final String DEFAULT_ADMIN_PWD = "adminpwd";
	
	/**
	 * TODO: move to configurable place
	 */
	public static final int MAX_BAD_PWDS_LOGINS = 6;
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * Ensures that an user with login "admin" exists; if created the user is disabled by default.
	 */
	public void ensureAdminUser() {
		
		User user = userRepository.findByLogin( DEFAULT_ADMIN_LOGIN );
		
		if ( user != null ) { return; }
		
		// TODO get email from application.properties or similar configuration
		create(DEFAULT_ADMIN_LOGIN, LoginService.DEFAULT_ADMIN_PWD, "test@greyshine.de", false);
	}
	
	@Transactional
	public LoginState login(String login, String password) {
		
		if ( Utils.isBlank( login ) || Utils.isBlank( password ) ) { return LoginState.BAD_REQUEST; }
		
		final User user = userRepository.findByLogin( login );
		
		if ( user == null ) { 
			return LoginState.UNKNWON_USER;
		}
		
		if ( !user.isActive() ) {
			return LoginState.INACTIVE;
		}
		
		
		if ( user.getFailedLogins() >= MAX_BAD_PWDS_LOGINS ) {
			return LoginState.BAD_PASSWORD_COUNT;
		}
		
		
		if ( !user.getPassword().equals( passwordSha512(  password ) ) ) {
			user.increaseBadPasswordCount();
			return LoginState.BAD_PASSWORD;
		}
		
		user.resetBadPasswordCount();
		
		return LoginState.OK;
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

	public String passwordSha512(String password) {
		
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
	
	public enum LoginState {
		OK,
		UNKNWON_USER,
		INACTIVE,
		BAD_PASSWORD,
		BAD_PASSWORD_COUNT,
		BAD_REQUEST;
	}
	
}
