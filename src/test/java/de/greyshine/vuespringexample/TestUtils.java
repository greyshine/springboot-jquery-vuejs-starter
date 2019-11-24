package de.greyshine.vuespringexample;

import org.json.JSONObject;
import org.springframework.test.web.servlet.MvcResult;

import de.greyshine.vuespringexample.utils.Utils;

public final class TestUtils {
	
	private TestUtils() {}
	
	/**
	 * @param resultHandler
	 * @return
	 */
	public static JSONObject extractJsonObject(MvcResult resultHandler) {
		
		try {

			final String data = resultHandler.getResponse().getContentAsString();
			return new JSONObject( data );
			
		} catch (Exception e) {
			throw Utils.toRuntimeException( e );
		}
	}
	

}
