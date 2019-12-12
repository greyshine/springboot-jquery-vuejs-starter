package de.greyshine.vuespringexample.web;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greyshine.vuespringexample.utils.Utils;

public class GeolocationDbComLocator implements IpLocator {

	private static final Logger LOG = LoggerFactory.getLogger( GeolocationDbComLocator.class );
	
	private final static String WWW = "https://geolocation-db.com/json/"; 
	
	private static final Utils.Timetrackings timetrackings = new Utils.Timetrackings();
	
	@Override
	public Location get(final String ip, long millisTimeout) {
		
		if ( Utils.isBlank( ip ) ) { return null; }
		else if ( millisTimeout < 1 ) { return requestLocation(ip); }
		
		LOG.debug( "fetch [ip={}, timeout={}ms]", ip, millisTimeout );
		
		try {
			return supplyAsync( ()->requestLocation(ip) ).get(millisTimeout, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			LOG.debug( "{}", Utils.toString(e) );
		} catch (Exception e) {
			LOG.error( "{}", Utils.toString(e), e );
		}
		
		return null;
	}
	
	public Location requestLocation(String ip) {
		
		final Location location = timetrackings.execute( ()->{
		
			final StringBuilder sb = new StringBuilder();
			Double latitude = null, longitude = null;
			
			final long starttime = System.currentTimeMillis();
			
			try ( InputStreamReader isr = new InputStreamReader( new URL( WWW+ip ).openStream(), "UTF-8" ) ) {
			
				while( isr.ready() ) {
					sb.append( (char)isr.read() );
				}
				
				final JSONObject json = new JSONObject( sb.toString() );
				LOG.debug( "{}", json );
				
				if ( !"not found".equalsIgnoreCase(String.valueOf(json.get("latitude"))) && !"not found".equalsIgnoreCase(String.valueOf(json.get("longitude"))) ) {
					latitude = json.getDouble( "latitude" );
					longitude = json.getDouble( "longitude" );	
				}
				
			} catch (Exception e) {
				LOG.error( "exception ip={}, resp={}: {}", ip, sb.toString(), Utils.toString( e ), e );
			}
			
			return IpLocator.buildLocation(latitude, longitude, System.currentTimeMillis()-starttime);
			
		} );
		
		LOG.debug( "runtimes: {}", timetrackings );
		return location;
	}
}
