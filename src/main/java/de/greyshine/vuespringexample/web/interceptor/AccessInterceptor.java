package de.greyshine.vuespringexample.web.interceptor;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.greyshine.vuespringexample.annotations.Access;
import de.greyshine.vuespringexample.utils.Utils;
import de.greyshine.vuespringexample.web.LoginController;

/**
 * Checks @Access annotations... 
 *
 */
public class AccessInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOG = LoggerFactory.getLogger( AccessInterceptor.class );

	@Autowired
	private LoginController loginController;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	
		final HandlerMethod handlerMethod = handler instanceof HandlerMethod ? (HandlerMethod)handler : null;
		if ( handlerMethod == null ) {
			return true;
		}
		
		Access access = handlerMethod.getMethodAnnotation( Access.class );
		access = access != null ? access : handlerMethod.getBeanType().getDeclaredAnnotation( Access.class );
		
		if ( access == null ) { return true; }
		
		
		final Set<String> userRights = loginController.getSessionUserRights(request);

		if ( userRights == null ) {
			
			// TODO: extra log illegal access attempt
			
			// application demands a logged-in user but none is logged-in			
			response.sendError( HttpStatus.UNAUTHORIZED.value() );
			return false;
		}
		
		// no specific right demanded by annotation
		// and we do have a logged in user
		if ( Utils.isAllBlank( access.value() ) ) {
			return true;
		}
		
		// is any right given by @Access in the users' set of rights
		for(String aRight : access.value()) {
			
			if ( Utils.isBlank( aRight ) ) { 
				LOG.warn("Found blank right definition at {}", handlerMethod); 
				continue;
			}
			
			for( String userRight : userRights ) {
				if ( Utils.isBlank( userRight ) ) { continue; }
				else if ( userRight.equalsIgnoreCase( aRight ) ) { 
					// granted access :-)
					return true;
				}
			}
		}
		
		// TODO: extra log illegal access attempt
		
		response.sendError( HttpStatus.UNAUTHORIZED.value() );
		return false;
	}	
}
