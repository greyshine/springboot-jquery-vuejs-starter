package de.greyshine.vuespringexample.web.interceptor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.greyshine.vuespringexample.MdcConstants;
import de.greyshine.vuespringexample.services.WebLogService;
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
	
	private static final AtomicLong REQUEST_COUNT = new AtomicLong(0L);
	
	@Autowired
	private LoginController loginController;

	@Autowired
	private WebLogService webLogService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		final LocalDateTime timestamp = LocalDateTime.now();
		final boolean doLog = webLogService.doLog( request.getRequestURI() ); 
		
		final String requestId = UUID.randomUUID().toString()+"."+REQUEST_COUNT.getAndAdd( 1 );
		
		String user = getUserBySession( request );
		user = Utils.isNotBlank( user ) ? user : loginController.getLoginByToken( request );
		user = Utils.isNotBlank( user ) ? user : null;
		
		MDC.put( MdcConstants.ID, requestId );
		MDC.put( MdcConstants.TIME_MILLIS, String.valueOf(timestamp.atZone( ZoneId.systemDefault() ).toInstant().toEpochMilli()) );
		MDC.put( MdcConstants.TIME, Utils.formatDate("yyyy-MM-dd'T'HH.mm.ss.SSS", timestamp ) );
		MDC.put( MdcConstants.IP, Utils.getIp( request ) );
		MDC.put( MdcConstants.USER, user );
		
		if ( doLog ) {
			webLogService.log( timestamp, requestId, Utils.getIp(request), request.getRequestURI(), request.getParameterMap(), user);		
		}
		
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		
		if ( webLogService.doLog( request.getRequestURI() ) ) {

			final String requestId = MDC.get( MdcConstants.ID );
			final long starttime = Long.valueOf( MDC.get( MdcConstants.TIME_MILLIS ) );
			final String ipAddress = MDC.get( MdcConstants.IP );
			
			webLogService.logFinish( requestId, starttime, ipAddress, ex );	
		}
	}
	
	private String getUserBySession( HttpServletRequest request ) {
		
		final HttpSession httpSession = request.getSession( false );
		String user = httpSession == null ? null : (String)httpSession.getAttribute( "login" );
		
		return Utils.trimToNull( user );
	}
}
