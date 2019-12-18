package de.greyshine.vuespringexample;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import de.greyshine.vuespringexample.services.WeblogEntityService;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@EnableAsync
public class Main {
	
	private static final Logger LOG = LoggerFactory.getLogger( Main.class );

	@SuppressWarnings("unused")
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private WeblogEntityService weblogEntityService;
	
	/**
	 * @return
	 */
	@Bean
	public Executor taskExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(1000);
		executor.setThreadNamePrefix("ThreadExecutor-");
		executor.initialize();
		return executor;
	}
	
	public static void main(String... args) {

		// simple, too easy way :-(
		// SpringApplication.run( Main.class, args );
		final SpringApplication sa = new SpringApplication(Main.class);

		// final Properties properties = new Properties();
		// properties.put( "spring.resources.static-locations", "classpath:/web" );
		// sa.setDefaultProperties( properties );

		sa.run(args);
	}

}
