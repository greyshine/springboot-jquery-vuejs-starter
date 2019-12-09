package de.greyshine.vuespringexample.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import de.greyshine.vuespringexample.test.UserInfo;
import de.greyshine.vuespringexample.services.UserService;
import de.greyshine.vuespringexample.web.LoginController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UsersWebIT {

	public static final Logger LOG = LoggerFactory.getLogger(UsersWebIT.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Test
	public void testListUsers_ADMIN() throws Exception {
		
		final String token = UserInfo.ADMIN.loginRest(mockMvc, userService);

		mockMvc.perform( get("/rest/users")
				        .header( LoginController.HEADER_AUTHORISATION, "BASIC "+ token)
				        )
		        .andExpect(status().isOk())
				.andDo(resultHandler -> LOG.info("{}", resultHandler.getResponse().getContentAsString()));
	}
	
	@Test
	public void testListUsers_TEST1() throws Exception {
		
		final String token = UserInfo.TEST1.loginRest(mockMvc, userService);

		mockMvc.perform( get("/rest/users")
				        .header( LoginController.HEADER_AUTHORISATION, "BASIC "+ token)
				        )
		        .andExpect(status().isOk())
				.andDo(resultHandler -> LOG.info("{}", resultHandler.getResponse().getContentAsString()));
	}

}
