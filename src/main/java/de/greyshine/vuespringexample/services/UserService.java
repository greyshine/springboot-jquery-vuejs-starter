package de.greyshine.vuespringexample.services;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.greyshine.vuespringexample.annotations.Access;
import de.greyshine.vuespringexample.db.entity.User;
import de.greyshine.vuespringexample.db.repos.UserRepository;
import de.greyshine.vuespringexample.utils.Utils;

@Service
public class UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	
	public static final String DEFAULT_ADMIN_LOGIN = "admin";
	/**
	 * Change that after first start of app!
	 */
	public static final String DEFAULT_ADMIN_PWD = "adminpwd";
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * Ensures that an user with login "admin" exists; if created the user is
	 * disabled by default.
	 */
	@Transactional
	public void ensureAdminUser() {

		User user = userRepository.findByLogin(DEFAULT_ADMIN_LOGIN);

		if (user != null) {
			return;
		}

		// TODO get email from application.properties or similar configuration
		create(DEFAULT_ADMIN_LOGIN, DEFAULT_ADMIN_PWD, "test@greyshine.de", new String[]{Access.RIGHT_ADMIN}, false);
	}
	
	@Transactional
	public User create(String login, String password, String email, String[] rights, boolean active) {

		Assert.hasText(login, "login is blank");
		Assert.hasText(password, "password is blank");
		Assert.hasText(email, "email is blank");
		
		User user = new User();
		user.setLogin(login.strip());
		user.setPassword( getPasswordCrypted(password) );
		user.setActive(active);
		user.setEmail(email.strip());
		
		for(String right : rights==null?new String[0]:rights) {
			user.addRight( right );
		}
		
		user = userRepository.save(user);

		LOG.debug("created: {}", user);
		
		return user;
	}
	
	/**
	 * TODO: do some crazy special letter handling so db stored value is not reverse enginerable.
	 * @param password
	 * @return
	 */
	public static String getPasswordCrypted(String password) {

		if (password == null || password.strip().isBlank()) {
			return null;
		}

		try {

			final MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update(password.getBytes("utf8"));
			final String encryptedPassword = String.format("%0128x", new BigInteger(1, digest.digest())); 
			return encryptedPassword;

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	@Transactional
	public Set<String> getRights(String login) {
		
		if ( Utils.isBlank( login ) ) { return Collections.emptySet(); }
		
		final User user = userRepository.findByLogin( login );
		return user == null ? Collections.emptySet() : new HashSet<>( user.getRights() );
	}

	@Transactional
	public void cleanSessionToken(String login) {
		
		final User user = userRepository.findByLogin( login );
		if ( user == null ) { return; }
		
		user.setSessionToken( null );
	}

	@Transactional
	public boolean setActiveState(String login, boolean activeState) {
		
		final User user = Utils.isBlank( login ) ? null : userRepository.findByLogin( login );
		
		if ( user == null ) { return false; }
		
		user.setActive( activeState );
		
		return true;
	}

	@Transactional
	public boolean isLogin(String login) {
		return userRepository.findByLogin( login ) != null;
	}
	
	
}
