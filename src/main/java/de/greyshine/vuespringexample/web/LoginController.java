package de.greyshine.vuespringexample.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.greyshine.vuespringexample.db.entity.User;
import de.greyshine.vuespringexample.services.LoginService;
import de.greyshine.vuespringexample.services.LoginService.LoginState;
import de.greyshine.vuespringexample.services.UserService;
import de.greyshine.vuespringexample.utils.Utils;

@RestController
public class LoginController {
	
	private static final Logger LOG = LoggerFactory.getLogger( LoginController.class );

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private UserService userService;
	
	public static final String HEADER_WWWAUTHENTICATE = "WWW-Authenticate";
	public static final String HEADER_AUTHORISATION= "Authorisation";
	
	@PostMapping( value="/rest/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public Token loginRest(@RequestParam(value="login") String login, @RequestParam(value="password") String password, HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		final LoginState loginState = loginService.login( login, password );
		
		LOG.info( "LoginState [login={}]: {}", login, loginState );
		
		if ( LoginState.OK != loginState ) {
			
			res.sendError( HttpStatus.UNAUTHORIZED.value() );
			return null;
		}
		
		return new Token( loginService.getSessionToken( login, true ) );
	}
	
	@PostMapping( value="/ajax/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public Status loginAjax(@RequestParam(value="login") String login, @RequestParam(value="password") String password, HttpServletRequest req, HttpServletResponse res) {
		
		final LoginState loginState = loginService.login( login, password );
		
		if ( LoginState.OK != loginState ) {
		
			LoginService.LOG_USERLOGIN.error( "Login failure [user={}, loginState={}, ip={}]", login, loginState.name(), Utils.getIp( req ) );
			
			// https://stackoverflow.com/a/6937030/845117
			res.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
			
			// I do not know what and how to set the value, what is a realm what is basic how to cope with www form logins...; may someone explain to a foreigner, please: kuemmel.dss@gmx.de
			//res.setHeader( "WWW-Authenticate" , "Basic realm=\"myRealm\"");
			// probably see: https://blog.restcase.com/restful-api-authentication-basics/
			// sending this  
			//res.setHeader("WWW-Authenticate", "Basic realm=\"WTF_is_a_realm-questionMark\"");
			// will have the browser to open a browser-native login screen.
			return new Status( null ) ;
			
		} else {
			
			 final HttpSession httpSession = initNewHttpSession( login, req );
			 return new Status( httpSession == null ? null : login );
		}
	}
	
	@GetMapping( value="/rest/logout", produces = MediaType.APPLICATION_JSON_VALUE)
	public Status logoutRest(HttpServletRequest req) {
		return logoutAjax(req);
	}
	
	@GetMapping( value="/ajax/logout", produces = MediaType.APPLICATION_JSON_VALUE)
	public Status logoutAjax(HttpServletRequest req) {
		
		final HttpSession httpSession = req.getSession( false );
		final String login = httpSession == null ? null : (String)httpSession.getAttribute( "login" );
		
		if ( login != null ) {

			final String sessionId = httpSession.getId();
			httpSession.invalidate();
			userService.cleanSessionToken( login );
			LOG.info( "logout [login="+ login +", sessionId="+ sessionId +"]" );
		}
		
		return new Status( null ) ;
	}
	
	private HttpSession initNewHttpSession(String login, HttpServletRequest req) {
		
		if ( req == null ) { return null; }
		if ( login == null || login.strip().isBlank() ) { return null; }
		
		final String sessionToken = loginService.getSessionToken(login, false);
		
		if ( sessionToken == null ) { return null; }
		
		final HttpSession httpSession = req.getSession( true );
		LOG.info( "initNewHttpSession [login={}, session={}, token={}]", login, httpSession.getId(), sessionToken );
		
		httpSession.setAttribute( "login" , login);
		httpSession.setAttribute( "token" , sessionToken );
		
		return httpSession;
	}
	
	/**
	 * If a {@link User} is logged-in a {@link List} will be returned. The list may be empty when no right is associated,<br/>
	 * Will return <tt>null</tt> when no {@link User} is logged-in.
	 *  
	 * @param req
	 * @return
	 */
	@Transactional
	public Set<String> getSessionUserRights(HttpServletRequest req) {
		
		final HttpSession httpSession = req.getSession(false);

		String login = httpSession == null ? null : (String)httpSession.getAttribute( "login" );
		
		if ( Utils.isBlank( login ) ) {
			
			LOG.info( "token for user rights: {}", req.getHeader( HEADER_AUTHORISATION ) );
			
			final String token = Utils.executeSafe( () -> {

				final String t = (String)req.getHeader( HEADER_AUTHORISATION );
				return t.substring( t.strip().indexOf( ' ' ) ).trim();
			} );
			
			login =  Utils.isBlank( token ) ? null : loginService.getLoginByToken( token );
		}
		
		return login == null ? null : userService.getRights( login );
	}

	@GetMapping( value="status", produces = MediaType.APPLICATION_JSON_VALUE)
	public Status status(HttpServletRequest req) {
		
		return new Status( getLoggedInName(req) );
	};
	
	@PostMapping( value="/ajax/passwordreset", produces = MediaType.APPLICATION_JSON_VALUE )
	public Status passwordReset( @RequestParam(value="email") String email, HttpServletRequest request ) {
		
		// TODO: prevent too many tries from same address in a certain timeframe
		
		LOG.info( "passwordReset: {} by {}", email, Utils.getIp( request ) );
		
		loginService.requestPasswordRenewal( email );
		return new Status( "ok" );
	}
	
	/**
	 * @param req
	 * @return currently logged-in user
	 */
	public String getLoggedInName(HttpServletRequest req) {
		
		if ( req == null ) { return null; }
		
		final HttpSession httpSession = req.getSession( false );
		
		String login = null;
		
		if ( httpSession != null && httpSession.getAttribute("login") != null ) {
			login = ((String)httpSession.getAttribute("login"));
			login = Utils.strip( login );
		}
		
		return login;
	}
	
	
	@GetMapping( value="test")
	public Data test() {		
		return new Data( LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) );
	}
	
	
	public static class Status {
		
		public String login; 
		Status(String login) { this.login = login; }
	}
	
	public static class Data {
		
		public String data; 
		Data(String data) { this.data = data; }
	}
		
	public static class Token {
		
		public String token; 
		Token(String token) { this.token = token; }
	}
	
	
}
