package de.greyshine.vuespringexample.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.transaction.annotation.Transactional;

import de.greyshine.vuespringexample.UserInfo;
import de.greyshine.vuespringexample.services.UserService;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LoginControllerWebIT {
	
	public static final Logger LOG = LoggerFactory.getLogger(LoginControllerWebIT.class);
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserService userService;
	
	@Test
	@Transactional
	public void testPasswordReset() throws Exception {

		UserInfo.TEST1.ensureDbExistance( userService );
		
		mockMvc.perform( post("/ajax/passwordreset")
		        		.param("email", UserInfo.TEST1.email )
		        )
        .andExpect(status().isOk())
		.andDo(resultHandler -> LOG.info("{}", resultHandler.getResponse().getContentAsString()));
	}

}
