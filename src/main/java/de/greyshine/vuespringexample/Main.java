package de.greyshine.vuespringexample;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Main {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public static void main(String... args) {
		
		// simple, too easy way :-(
		// SpringApplication.run( Main.class, args );
		final SpringApplication sa = new SpringApplication( Main.class );
		
		final Properties properties = new Properties();
		properties.put( "spring.resources.static-locations", "classpath:/web" );
		//sa.setDefaultProperties( properties );
		
		sa.run( args );
	}

}
