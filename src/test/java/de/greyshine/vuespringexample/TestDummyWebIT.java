package de.greyshine.vuespringexample;

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


@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
@AutoConfigureMockMvc
public class TestDummyWebIT {
	
	public static final Logger LOG = LoggerFactory.getLogger( TestDummyWebIT.class );
	
	@Autowired
    private MockMvc mockMvc;
	
	@Test
	public void test() throws Exception {
		
		mockMvc.perform( get("/ajax/status") )
		       .andExpect( status().isOk() )
		       .andDo( resultHandler -> LOG.info("{}", resultHandler.getResponse().getContentAsString() ) );
		
		
	}
	
}
