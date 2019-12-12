package de.greyshine.vuespringexample.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.greyshine.vuespringexample.utils.Utils;
import de.greyshine.vuespringexample.web.IpLocator;

/**
 * Note: No access to MDC values on @Async methods!
 */
@Service
public class WebLogService {
	
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger( WebLogService.class );
	
	private static final Logger LOG_REQUESTS = LoggerFactory.getLogger( "WEBREQUEST" );
	
	@Value( "${ipLocator.timeout:3000}" )
	private long ipLookupTimeout;
	
	private static final Map<String,JSONObject> LOGS = new HashMap<>();
	
	@Autowired
	private IpLocator ipLocator;

	@Autowired
	private Filter webAccessFilter;
	
	@Autowired
	private WeblogEntityService weblogEntityService;
	
	public boolean doLog(String requestUri) {
		return webAccessFilter == null ? Filter.DEFAULT.doLog(requestUri) : webAccessFilter.doLog(requestUri);
	}
	
	public void log(LocalDateTime time, String requestId, String ipAddress, String requestUri, Map<String,String[]> params, String user) {
		
		if ( !doLog(requestUri) ) { return; }
		
		user = Utils.trimToNull(user);
		
		final JSONObject ps = new JSONObject();
		params.keySet().forEach( k -> {
			
			final String[] values = "password".equalsIgnoreCase( k.strip() ) ? new String[] {"***"} : params.get( k );
			
			if ( values == null ) {
				ps.put( k, JSONObject.NULL);
			} else if ( values.length == 1 ) {
				ps.put( k, values[0]);
			} else {
				final JSONArray vs = new JSONArray();
				Stream.of( values ).forEach( v -> vs.put( v ) );
				ps.put( k, vs );
			}
			
			params.get( k );
		} );
		
		final JSONObject json = new JSONObject();
		json.put( "id", requestId);
		json.put( "t", Utils.formatDate( Utils.LOCALDATETIME_FORMAT, time) );
		json.put( "ip", ipAddress);
		json.put( "u", user == null ? "?" : user );
		json.put( "uri", requestUri);
		json.put( "ps", ps);
		
		LOGS.put( requestId, json );
		
		LOG_REQUESTS.info( json.toString() );
	}

	public void logFinish(String requestId, long starttime, String ipAddress, Exception exception) {
		
		final JSONObject json = LOGS.remove( requestId );
		if ( json == null ) { return; }
		
		// calculated before fetching IP geo info which is not part of the actual request handling
		final int handlingDurance = (int)(System.currentTimeMillis() - starttime); 
		
		json.put( "ltln", ipLocator.get( ipAddress, this.ipLookupTimeout ) ) ;
		json.put( "d", handlingDurance );
		json.put( "e", exception == null ? JSONObject.NULL : exception.getClass().getTypeName() );
		
		LOG_REQUESTS.info( json.toString() );
		
		// delegate
		final boolean isCacheFull = weblogEntityService.enqueue( json );
		if ( isCacheFull ) {
			weblogEntityService.persist();
		}
	}
	
	/**
	 * Filtering requests to be logged.  
	 */
	@FunctionalInterface
	public interface Filter {
		
		boolean doLog(String requestUri);
		
		public static Filter DEFAULT = requestUri -> {
			
			if ( Utils.isBlank( requestUri ) ) { return false; }
			else if ( requestUri.startsWith( "/js/" ) ) { return false; }
			else if ( requestUri.startsWith( "/css/" ) ) { return false; }
			else if ( requestUri.equalsIgnoreCase("/favicon.ico") ) { return false; }
			else if ( requestUri.startsWith("/icons/") ) { return false; }
			
			return true;
		};
	}
}
