package de.greyshine.vuespringexample.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collection of static utility methods.
 */
public final class Utils {
	
	private static final Logger LOG = LoggerFactory.getLogger( Utils.class );
	
	public static final Charset CHARSET_UTF8 = Charset.forName( "UTF-8" );

	public static final String LOCALDATE_FORMAT = "yyyy-MM-dd";
	public static final String LOCALDATETIME_FORMAT = LOCALDATE_FORMAT+"'T'HH:mm:ss.SSS";

	private Utils() {}

	public static String toString(Object object) {
		
		if ( object == null ) {
			return "null";
		}
		
		else if ( object instanceof LocalDate ) {
			final LocalDate ld = (LocalDate) object;
			return ld.format( DateTimeFormatter.ISO_DATE );
		}
		
		else if ( object instanceof LocalDateTime ) {
			final LocalDateTime ldt = (LocalDateTime) object;
			return ldt.format( DateTimeFormatter.ISO_DATE_TIME );
		}
		
		else if ( object instanceof Throwable ) {
			final Throwable t = (Throwable) object;
			return t.getClass().getTypeName()+" [message="+ t.getMessage() +", cause="+ toString(t.getCause()) +"]";
		}
		
		return String.valueOf( object );
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
	public static boolean isAllBlank(String... values) {
		
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
	
	public static String trim(String value) {
		return value == null ? null : value.trim();
	}
	
	public static String trimToNull(String value) {
		return isBlank(value) ? null : value.trim();
	}
	
	public static String trimToNull(String value, Function<String,String> modifierFunctionOnNotNull) {
		if ( isBlank(value) ) { return null; }
		return modifierFunctionOnNotNull == null ? value.trim() : modifierFunctionOnNotNull.apply( value );
	}
	
	public static String trimToEmpty(String value) {
		return isBlank(value) ? "" : value.trim();
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
	public interface Runnable2 {
		void run() throws Exception;
	}
	
	@FunctionalInterface
	public interface Supplier2<T> {
		T get() throws Exception;
	}
	
	@FunctionalInterface
	public interface Function2<S,T> {
		T apply( S argument ) throws Exception;
	}

	public static String formatDate(String format, LocalDateTime localDateTime) {
		if ( format == null || localDateTime == null ) { return null; }
		
		// TODO: optimize by using cached DateTimeFormatter 
		return DateTimeFormatter.ofPattern( format ).format( localDateTime );
	}
	
	public static LocalDateTime parseLocalDateTime(String format, String value) {
		
		if ( isBlank(value) || isBlank(format) ) { return null; }
		
		// TODO: optimize by using cached DateTimeFormatter
		try {
		
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern( format );
	        return LocalDateTime.parse(value, formatter);
			
		} catch (Exception e) {
			LOG.warn( "{}", e );
			return null;
		}
	}

	public static boolean equals(String s1, String s2, boolean ignoreCasing) {
		if ( s1 == null || s2 == null ) { return false; }
		if ( s1 == s2 ) { return true; }
		return !ignoreCasing ? s1.equals( s2 ) : s1.equalsIgnoreCase(s2);
	}
	
	public static boolean equals(Long l1, Long l2) {
		if ( l1 == null || l2 == null ) { return false; }
		return l1 == l2;
	}

	public static String getResource(String name, Charset charset) throws IOException {
		
		charset = charset != null ? charset : CHARSET_UTF8;
		
		final InputStream is =  ClassLoader.getSystemClassLoader().getResourceAsStream( name );
		
		if ( is == null ) { return null; }
		
		final StringBuilder sb = new StringBuilder();
		
		try (final Reader reader = new InputStreamReader( is , charset )) {
			int c = -1;
			while( (c = reader.read()) != -1 ) {
				sb.append( (char)c );
			}
	    }
		
		return sb.toString();
	}

	public static void threadWait(long millis) {
		
		final long waitUntil = System.currentTimeMillis() + millis;
		
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "wait until "+ new Date(waitUntil) );	
		}
		
		while( System.currentTimeMillis() < waitUntil ) {
			
			final long waitTime = waitUntil - System.currentTimeMillis();
			
			if ( waitTime < 1 ) { continue; }
			
			synchronized ( Thread.currentThread() ) {
				try {
					Thread.currentThread().wait( waitTime );
				} catch (InterruptedException e) {
					LOG.warn("error waiting", e);
				}				
			}
		}
	}
	
	public static void close(Closeable... closeables) {
		if ( closeables == null ) { return; }
		for(Closeable c : closeables) {
			try {
				c.close();
			} catch (Exception e) {
				// intended ignore
			}
		}
	}

	public static String trimOrDefaultIfBlank(String value, Supplier<String> supplier ) {
		if ( isNotBlank(value) ) { return value.strip(); }
		return supplier == null ? null : supplier.get(); 
	}

	public static <T,S extends Closeable> T executeAndClose(S closable, Function2<S,T> function) throws Exception {
		
		try {
		
			return function == null ? null : function.apply( closable );
			
		} finally {
			close( closable );
		}
	}

	public static String stringToString(String s) {
		return s == null ? "null" : "\""+ s.replace("\"", "\\\"") +"\"";
	}

	public static <T> Stream<T> stream(Enumeration<T> e) {
		
		// https://stackoverflow.com/a/33243700/845117
	    return StreamSupport.stream(
	    		
	        new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
	            
	        	public boolean tryAdvance(Consumer<? super T> action) {
	                if(e != null && e.hasMoreElements()) {
	                    action.accept(e.nextElement());
	                    return true;
	                }
	                return false;
	            }
	            public void forEachRemaining(Consumer<? super T> action) {
	                while(e != null && e.hasMoreElements()) { action.accept(e.nextElement()); }
	            }
	    }, false);
	}

	public static Class<?> getClass(Object object) {
		return object == null ? null : object.getClass();
	}

	public static Long parseLong(String string) {
		try {
			return isBlank(string) ? null : Long.parseLong( string.strip() );
		} catch(Exception e) {
			return null;
		}
	}
	
	public static class Timetrackings {
		
		private final Object SYNC = new Object();
		
		private BigDecimal sum = BigDecimal.ZERO;
		private long count = 0;
		
		public void add(Double value) {
			
			if ( value == null ) { return; }
			
			synchronized ( SYNC ) {
				sum = sum.add( BigDecimal.valueOf( value ) );
				count++;
			}
		}
		
		public void add(Long value) {
			
			if ( value == null ) { return; }
			
			synchronized ( SYNC ) {
				sum = sum.add( BigDecimal.valueOf( value ) );
				count++;
			}
		}
		
		public void execute( Runnable2 runnable ) {
			
			if ( runnable == null ) { return; }
			
			final long starttime = System.currentTimeMillis();
			
			try {
				
				runnable.run();
			
			} catch(Exception e) {
				throw toRuntimeException(e);
			} finally {
				add( System.currentTimeMillis()-starttime );
			}
		}
		
		public <T> T execute( Supplier2<T> supplier ) {
			
			if ( supplier == null ) { return null; }
			
			final long starttime = System.currentTimeMillis();
			
			try {
				
				return supplier.get();
			
			} catch(Exception e) {
				throw toRuntimeException(e);
			} finally {
				add( System.currentTimeMillis()-starttime );
			}
		}
		
		public double getSum() {
			return sum.doubleValue();
		}
		
		public long getCount() {
			return count;
		}
		
		public double getAverage() {
			return count == 0 ? 0.0 : sum.divide( BigDecimal.valueOf(count), 7, RoundingMode.HALF_UP).setScale( 6, RoundingMode.HALF_UP).doubleValue();
		}
		
		@Override
		public String toString() {
			synchronized (SYNC) {
				return Timetrackings.class.getSimpleName()+" [avg="+ getAverage() +", count="+ count +", sum="+ sum.toPlainString() +"]";	
			}
		}
		
	}
}
