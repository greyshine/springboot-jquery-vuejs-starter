package de.greyshine.vuespringexample.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

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
	 * Checks if the {@link String} array is <tt>null</tt>, has a length of 0 or only contains blank values.
	 * @param values
	 * @return
	 */
	public static boolean isBlank(String[] values) {
		
		if ( values == null || values.length == 0 ) { return true; }
		
		for(String aValue : values) {
			if ( !isBlank(aValue) ) { return false; }
		}
		
		return true;
	}
	
	/**
	 * Null safe String strip.
	 * @param value
	 * @return
	 */
	public static String strip(String value) {
		return value == null ? null : value.strip();
	}
	
	public static String trim(String string) {
		return string == null ? null : string.trim();
	}
	
	public static String trimToNull(String value) {
		return value == null || isBlank(value) ? null : value.trim();
	}
	
	public static String trimToEmpty(String value) {
		return value == null || isBlank(value) ? "" : value.trim();
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

	
	/**
	 * 
	 * @param exception 
	 * @return the given {@link Exception} if it is a {@link RuntimeException}, otherwise it is wrapped into a {@link RuntimeException}
	 */
	public static RuntimeException toRuntimeException(Exception exception) {
		
		if ( exception == null ) {
			// intended NPE throwing. Illegal argument could be misleading from program code. But this is a fatal wrong call/usage of this method
			throw new NullPointerException("Method was called with null parameter. Never do tha - correct the program code!");
		}
		
		return exception instanceof RuntimeException ? (RuntimeException)exception : new RuntimeException(exception);  
		
	}

	/**
	 * Securely executes a function / supplier. If an {@link Exception} occurs and a proper exception {@link Function} is provided it is invoked for returning a value.<br/>
	 * 
	 * If execution fails <tt>null</tt> is returned.
	 * 
	 * @param <T>
	 * @param supplier
	 * @return
	 */
	public static <T> T executeSafe( Supplier2<T> supplier ) {
		return executeSafe( supplier, exception -> null ) ;
	}
	
	/**
	 * Securely executes a function / supplier. If an {@link Exception} occurs and a proper exception {@link Function} is provided it is invoked for returning a value.<br/>
	 * If no exception handler is provided <tt>null</tt> is returned.
	 * @param <T>
	 * @param supplier
	 * @param exceptionFunction
	 * @return
	 */
	public static <T> T executeSafe( Supplier2<T> supplier, Function2<Exception,T> exceptionFunction ) {
		
		if ( supplier == null ) {
			throw new IllegalArgumentException("No supplier provided");
		}
		
		try {
			
			return supplier.get();
			
		} catch(Exception e) {
			
			try {
				return exceptionFunction == null ? null : exceptionFunction.apply( e );	
			} catch (Exception e2) {
				throw toRuntimeException(e2);
			}
		}
	}
	
	@FunctionalInterface
	public interface Supplier2<T> {
		T get() throws Exception;
	}
	
	@FunctionalInterface
	public interface Function2<S,T> {
		T apply( S argument ) throws Exception;
	}
	
	
}
