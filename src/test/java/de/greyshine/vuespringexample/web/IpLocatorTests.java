package de.greyshine.vuespringexample.web;

import org.junit.Test;

public class IpLocatorTests {
	
	@Test
	public void test() {
		
		IpLocator il = new GeolocationDbComLocator();
		
		System.out.println( il.get( "10.0.0.138", 10000 ) );
		System.out.println( il.get( "195.3.90.145", 20000 ) );
		System.out.println( il.get( "78.35.18.22", 2000 ) );
		System.out.println( il.get( "81.173.192.113", 2000 ) );
		System.out.println( il.get( "81.173.192.117", 2000 ) );
	}

}
