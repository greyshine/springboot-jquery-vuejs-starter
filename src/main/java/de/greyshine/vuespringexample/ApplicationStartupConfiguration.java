package de.greyshine.vuespringexample;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import de.greyshine.vuespringexample.utils.Utils;

@Configuration
public class ApplicationStartupConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationStartupConfiguration.class);

	@Value("${environment:}")
	private String environment;

	@PostConstruct
	public void postConstruct() {
		
		environment = Utils.trimToNull( environment );
		LOG.info("environment={}", environment == null ? "<null>" : environment);

	}
}
