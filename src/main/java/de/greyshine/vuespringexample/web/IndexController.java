package de.greyshine.vuespringexample.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import de.greyshine.vuespringexample.db.entity.ConditionsAgreement;
import de.greyshine.vuespringexample.services.ContractAgreementService;
import de.greyshine.vuespringexample.utils.Utils;

@Controller
public class IndexController {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger( IndexController.class );
	
	@Autowired
	private LoginController loginController;
	
	@Autowired
	private ContractAgreementService contractAgreementService;
	
	@GetMapping(value="/plain", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String indexPlain() {
		return "hello world!";
	}
	
	@GetMapping(value="/", produces = MediaType.TEXT_HTML_VALUE)
	public String index(HttpServletRequest request) {
		
		final String login = loginController.getLoggedInName(request);
		if ( Utils.isBlank( login ) ) { return "index.login"; }
		
		final List<ConditionsAgreement> conditionsAgreements = contractAgreementService.getNeededConfirmations(login);
		request.setAttribute( "login", login );
		request.setAttribute( "conditionAgreements", conditionsAgreements );

		return !conditionsAgreements.isEmpty() ? "index.conditionAgreements" : "index";
	}

}
