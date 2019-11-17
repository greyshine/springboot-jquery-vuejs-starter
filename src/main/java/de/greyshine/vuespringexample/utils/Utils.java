package de.greyshine.vuespringexample.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

/**
 * Collection of static utility methods.
 */
public final class Utils {
	
	private Utils() {}

	public static String toString(Object object) {
		
		if ( object instanceof LocalDate ) {
			final LocalDate ld = (LocalDate) object;
			return ld.format( DateTimeFormatter.ISO_DATE );
		}
		
		else if ( object instanceof LocalDateTime ) {
			final LocalDateTime ldt = (LocalDateTime) object;
			return ldt.format( DateTimeFormatter.ISO_DATE_TIME );
		}
		
		return null;
	}
	
	/**
	 * Null safe blank check
	 * @param value
	 * @return
	 */
	public static boolean isBlank(String value) {
		return value == null || value.trim().isBlank();
	}
	
	public static boolean isNotBlank(String value) {
		return !isBlank(value);
	}
	
	/**
	 * Null safe String strip.
	 * @param value
	 * @return
	 */
	public static String strip(String value) {
		return value == null ? null : value.strip();
	}

	/**
	 * Fetch IP Address from a {@link HttpServletRequest}.
	 * 
	 * Taken from: https://stackoverflow.com/a/21529994/845117
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		
		if ( request == null ) { return null; }
		
		String ip = request.getHeader("X-Forwarded-For");  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getHeader("Proxy-Client-IP");  
	    }  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getHeader("WL-Proxy-Client-IP");  
	    }  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
	    }  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getHeader("HTTP_X_FORWARDED");  
	    }  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");  
	    }  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getHeader("HTTP_CLIENT_IP");  
	    }  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getHeader("HTTP_FORWARDED_FOR");  
	    }  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getHeader("HTTP_FORWARDED");  
	    }  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getHeader("HTTP_VIA");  
	    }  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getHeader("REMOTE_ADDR");  
	    }  
	    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	        ip = request.getRemoteAddr();  
	    }  
	
	    return ip;  
	}
}
