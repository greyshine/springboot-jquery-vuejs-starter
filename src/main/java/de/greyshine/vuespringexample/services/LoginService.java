package de.greyshine.vuespringexample.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.greyshine.vuespringexample.MdcConstants;
import de.greyshine.vuespringexample.db.entity.User;
import de.greyshine.vuespringexample.db.repos.UserRepository;
import de.greyshine.vuespringexample.email.EmailService;
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

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

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

		if (!user.getPassword().equals(UserService.getPasswordCrypted(password))) {
			user.increaseBadPasswordCount();
			return LoginState.BAD_PASSWORD;
		}

		MDC.put(MdcConstants.USER, user.getLogin());

		user.resetBadPasswordCount();

		return LoginState.OK;
	}

	@Transactional
	public String getSessionToken(String login, boolean buildWhenNull) {

		User user = userRepository.findByLogin(login);

		if (user == null) {
			LOG.info("no user found [login={}]", login);
			return null;
		}

		String sessionToken = user.getSessionToken();

		if (sessionToken == null) {

			sessionToken = createSessionId();
			user.setSessionToken(sessionToken);
		}

		return sessionToken;
	}

	public String createSessionId() {
		return new StringBuilder().append(UUID.randomUUID().toString()).append('.').append(sessionIds++).append('.')
				.append(System.currentTimeMillis()).toString();
	}

	public enum LoginState {
		OK, UNKNWON_USER, INACTIVE, BAD_PASSWORD, BAD_PASSWORD_COUNT, BAD_REQUEST;
	}

	@Transactional
	public String getLoginByToken(String token) {

		if (Utils.isBlank(token)) {
			return null;
		}

		final User user = userRepository.findBySessionToken(token);
		return user == null ? null : user.getLogin();
	}

	@Transactional
	public boolean requestPasswordRenewal(String email) {

		if (Utils.isBlank(email)) {
			return false;
		}

		email = email.strip();

		final String password = UUID.randomUUID().toString().toLowerCase().replace("-", "");

		Query q = em.createQuery("UPDATE User u " + "SET u.password = :password " + "WHERE u.email = :email "
				+ "AND u.login != :adminLogin ");

		q.setParameter("adminLogin", "admin");
		q.setParameter("email", email);
		q.setParameter("password", UserService.getPasswordCrypted(password));

		final int resultCount = q.executeUpdate();

		if (resultCount < 1) {
			LOG.warn("Reseting password for email '{}' failed. No such account.", email);
			return false;
		}

		q = em.createQuery("SELECT u.login " + "FROM User u " + "WHERE u.email = :email ");
		q.setParameter("email", email);

		final Map<String, String> emailVars = new HashMap<>(2);
		emailVars.put("user", String.valueOf(q.getResultList().get(0)));
		emailVars.put("password", password);

		emailService.send("new-password", EmailService.AddressTo.build(email), emailVars, null);
		return true;
	}
}
