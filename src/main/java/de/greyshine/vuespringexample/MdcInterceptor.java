package de.greyshine.vuespringexample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.greyshine.vuespringexample.utils.Utils;
import de.greyshine.vuespringexample.web.LoginController;

/**
 * 
 * http://logback.qos.ch/manual/mdc.html
 * 
 */
public class MdcInterceptor extends HandlerInterceptorAdapter {
	
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger( MdcInterceptor.class );
	
	@Autowired
	private LoginController loginController;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		String user = getUserBySession( request );
		user = Utils.isNotBlank( user ) ? user : loginController.getLoginByToken( request );
		user = Utils.isNotBlank( user ) ? user : "?";
		
		MDC.put( "ip", Utils.getIp( request ) );
		MDC.put( "user", user );
		
		return true;
	}
	
	private String getUserBySession( HttpServletRequest request ) {
		final HttpSession httpSession = request.getSession( false );
		String user =  httpSession == null ? null : (String)httpSession.getAttribute( "login" );
		return Utils.trimToNull( user );
	}
}
