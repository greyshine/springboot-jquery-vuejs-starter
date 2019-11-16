package de.greyshine.vuespringexample.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

	private static final Logger LOG = LoggerFactory.getLogger( IndexController.class );
	
	@Autowired
	private LoginController loginController;
	
	@GetMapping(value="/plain", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String indexPlain() {
		return "hello world!";
	}
	
	@GetMapping(value="/", produces = MediaType.TEXT_HTML_VALUE)
	public String index(HttpServletRequest req) {
		return loginController.getLoggedInName(req) == null ? "index.login" : "index";
	}

}
