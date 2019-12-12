package de.greyshine.vuespringexample.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.greyshine.vuespringexample.db.entity.Weblog;
import de.greyshine.vuespringexample.utils.Utils;

@Service
public class WeblogEntityService {
	
	private static final Logger LOG = LoggerFactory.getLogger( WeblogEntityService.class );

	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Size of less than 0 switches writing to DB off.
	 * Size of 0 does immediately write to DB.
	 */
	@Value( "${weblog.cache:0}" )
	private int weblogCacheSize;
	
	private static final List<Weblog> weblogCache = new ArrayList<>();
	
	@Async
	@Transactional
	public void persist() {
		
		if ( weblogCache.isEmpty() ) { return; }
		
		final List<Weblog> workoff = new ArrayList<>(); 
		
		synchronized ( weblogCache ) {
			workoff.addAll( weblogCache );
			weblogCache.clear();
		}
		
		workoff.forEach( weblogEntry -> {
			LOG.debug( "persist: {}", weblogEntry );
			em.persist( weblogEntry );
			}
		);
		
		LOG.info( "persisting {} entries", workoff.size() );
	}
	
	/**
	 * 
	 * @param json
	 * @return <code>true</code> when a persist must be invoked
	 */
	public boolean enqueue(JSONObject json) {
		
		LOG.debug( "{}", json );
		
		final String user = json.getString( "u" );
		
		final Weblog weblog = new Weblog();
		weblog.setId( json.getString( "id" ) );
		weblog.setTimestamp( Utils.parseLocalDateTime( Utils.LOCALDATETIME_FORMAT, json.getString("t")) );
		weblog.setUser( "?".equalsIgnoreCase( user ) ? null : Utils.trimToNull( user ) );
		weblog.setDurance( json.getInt( "d" ) );
		weblog.setIp( json.getString( "ip" ) );
		weblog.setUri( json.getString( "uri" ) );
		weblog.setParams( json.get( "ps" ).toString() );
		weblog.setLatlon( Utils.executeSafe( ()->json.getString( "ltln" ) ) );
		weblog.setException( Utils.executeSafe( ()->json.getString( "e" ) ) );
		
		synchronized ( weblog ) {
			weblogCache.add( weblog );	
			
			// ! caching ends in the problem that on shutdown a transactional bean cannot be referenced and thus the cache is not emptied 
			// TODO write stackoverflow request
			// return weblogCache.size() >= weblogCacheSize;
			return true;
		}
	}
}
