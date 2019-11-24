package de.greyshine.vuespringexample.services;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.greyshine.vuespringexample.db.entity.User;
import de.greyshine.vuespringexample.db.repos.UserRepository;
import de.greyshine.vuespringexample.utils.Utils;

@Service
public class LoginService {

	private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);

	public static final Logger LOG_USERLOGIN = LoggerFactory.getLogger("UserLogin");

	private static volatile long sessionIds = 0;
	

	/**
	 * TODO: move to configurable place
	 */
	public static final int MAX_BAD_PWDS_LOGINS = 6;

	@Autowired
	private UserRepository userRepository;

	@Transactional
	public LoginState login(String login, String password) {

		if (Utils.isBlank(login) || Utils.isBlank(password)) {
			return LoginState.BAD_REQUEST;
		}

		final User user = userRepository.findByLogin(login.strip());

		if (user == null) {
			return LoginState.UNKNWON_USER;
		}

		if (!user.isActive()) {
			return LoginState.INACTIVE;
		}

		if (user.getFailedLogins() >= MAX_BAD_PWDS_LOGINS) {
			return LoginState.BAD_PASSWORD_COUNT;
		}

		if (!user.getPassword().equals( UserService.getPasswordCrypted(password)) ) {
			user.increaseBadPasswordCount();
			return LoginState.BAD_PASSWORD;
		}

		user.resetBadPasswordCount();

		return LoginState.OK;
	}
	
	@Transactional
	public String getSessionToken(String login, boolean buildWhenNull) {
		
		User user = userRepository.findByLogin( login );
		
		if ( user == null ) { 
			LOG.info( "no user found [login={}]", login );
			return null;
		}

		String sessionToken = user.getSessionToken();
		
		if ( sessionToken == null ) {
			
			sessionToken = createSessionId();
			user.setSessionToken( sessionToken );
		}
		
		return sessionToken;
	}

	public String createSessionId() {
		return new StringBuilder().append( UUID.randomUUID().toString() ).append( '.' ).append( sessionIds++ ).append( '.' ).append( System.currentTimeMillis() ).toString();
	}

	public enum LoginState {
		OK, UNKNWON_USER, INACTIVE, BAD_PASSWORD, BAD_PASSWORD_COUNT, BAD_REQUEST;
	}

	@Transactional
	public String getLoginByToken(String token) {
		
		if ( Utils.isBlank( token ) ) { return null; }
		
		final User user = userRepository.findBySessionToken( token );
		return user == null ? null : user.getLogin();
	}
}
