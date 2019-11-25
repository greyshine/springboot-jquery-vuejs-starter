package de.greyshine.vuespringexample.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.greyshine.vuespringexample.services.LoginService;
import de.greyshine.vuespringexample.services.LoginService.LoginState;
import de.greyshine.vuespringexample.utils.Utils;

@RestController
@RequestMapping("/ajax")
public class LoginController {
	
	private static final Logger LOG = LoggerFactory.getLogger( LoginController.class );

	@Autowired
	private LoginService loginService;
	
	@PostMapping( value="/login")
	public Status login(@RequestParam(value="login") String login, @RequestParam(value="password") String password, HttpServletRequest req, HttpServletResponse res) {
		
		final LoginState loginState = loginService.login( login, password );
		
		if ( LoginState.OK != loginState ) {
		
			LoginService.LOG_USERLOGIN.error( "Login failure [user={}, loginState={}, ip={}]", login, loginState.name(), Utils.getIp( req ) );
			
			// https://stackoverflow.com/a/6937030/845117
			res.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
			
			// I do not know what and how to set the value, what is a realm what is basic how to cope with www form logins...; may someone explain to a foreigner, please: kuemmel.dss@gmx.de
			// when enabled, browser will show native login screen.
			//res.setHeader( "WWW-Authenticate" , "Basic realm=\"myRealm\"");
			
		} else {
			
			initNewHttpSession( login, req );
		}
		
		return new Status( LoginState.OK == loginState ? login : null ) ;
	}
	
	@GetMapping( value="logout")
	public Status logout(HttpServletRequest req) {
		
		final HttpSession httpSession = req.getSession( false );
		final String login = httpSession == null ? null : (String)httpSession.getAttribute( "login" );
		
		if ( login != null ) {
			final String sessionId = httpSession.getId();
			httpSession.invalidate();
			LOG.info( "logout [login="+ login +", sessionId="+ sessionId +"]" );
		}
		
		return new Status( null ) ;
	}
	
	private HttpSession initNewHttpSession(String login, HttpServletRequest req) {
		
		if ( req == null ) { return null; }
		if ( login == null || login.strip().isBlank() ) { return null; }
		
		final HttpSession httpSession = req.getSession( true );
		LOG.info( "initNewHttpSession [login="+ login +", session="+ httpSession.getId() +"]" );
		httpSession.setAttribute( "login" , login);
		
		return httpSession;
	}

	@GetMapping( value="status")
	public Status status(HttpServletRequest req) {
		
		return new Status( getLoggedInName(req) );
	};
	
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
	
	
	
}
