package de.greyshine.vuespringexample.utils;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTests {
	
	@Test
	public void testBlank() {
		
		Assert.assertTrue( Utils.isBlank( null ) );
		Assert.assertTrue( Utils.isBlank( "" ) );
		Assert.assertTrue( Utils.isBlank( " " ) );
		
		Assert.assertFalse( Utils.isBlank( "a" ) );
		Assert.assertFalse( Utils.isBlank( " a  " ) );
	}

}
