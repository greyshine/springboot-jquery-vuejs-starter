package de.greyshine.vuespringexample.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;

import de.greyshine.vuespringexample.TestUtils;
import de.greyshine.vuespringexample.services.UserService;
import de.greyshine.vuespringexample.utils.Utils;

public abstract class UserInfo {

	private static final Logger LOG = LoggerFactory.getLogger( UserInfo.class );
	
	public static final String EMAIL_DOMAIN = "test-check.com";
	
	public String login, password, email;
	public Boolean activeState = true;
	public String sessionToken = null;
	public String[] rights;
	
	public void init(UserService userService) {

		userService.cleanSessionToken( login );
		userService.setActiveState( login , activeState );
	}
	
	public UserInfo ensureDbExistance(UserService userService) {
		
		if ( !userService.isLogin( login ) ) {
			userService.create(login, password, email, new String[] { "right1" }, false);
		}
		
		if ( this.activeState != null ) {
			userService.setActiveState(login, activeState);
		}
		
		return this;
	}
	
	/**
	 * @param mockMvc
	 * @return session token
	 */
	public String loginRest(MockMvc mockMvc, UserService userService) {
		
		ensureDbExistance(userService);
		
		if ( sessionToken != null ) {
			LOG.warn( "sessionToken is not null [userInfo={}]" , this);
			logout();
		}
		
		try {
		
			mockMvc.perform( post("/rest/login")
						   .param("login", this.login )
					       .param("password", this.password)
					       )
				.andDo(resultHandler -> LOG.info("{}", resultHandler.getResponse().getContentAsString()))
				.andExpect(status().isOk())
				.andDo(resultHandler -> {
					final JSONObject json = TestUtils.extractJsonObject(resultHandler);
					sessionToken = json.getString("token");
				});
			
		} catch (Exception e) {
			throw Utils.toRuntimeException(e);
		}
		
		if ( Utils.isBlank( sessionToken ) ) {
			throw new IllegalStateException("Expected to have received a session token.");
		}	
		
		return sessionToken;
	}
	
	public void logout() {
		throw new UnsupportedOperationException("not implemented yet"); 
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() +" [login="+login+"]";
	}
	
	public static UserInfo ADMIN = new UserInfo() {
		{
			login = UserService.DEFAULT_ADMIN_LOGIN;
			password = UserService.DEFAULT_ADMIN_PWD; 
			email = "admin@"+EMAIL_DOMAIN;
		}
	};
	
	public static UserInfo TEST1 = new UserInfo() {
		{
			login = "test1";
			password = "test1pwd"; 
			rights = new String[] {"right1"};
			email = "test1@"+EMAIL_DOMAIN;
		}
	};
	

}
