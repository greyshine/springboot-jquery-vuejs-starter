package de.greyshine.vuespringexample;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDummy {
	
	public static final Logger LOG = LoggerFactory.getLogger( TestDummy.class );
	
	@Test
	public void test() {
		System.out.println( "testing works" );
	}
	
}
