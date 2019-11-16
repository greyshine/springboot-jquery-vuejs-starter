package de.greyshine.vuespringexample.services;

import org.springframework.stereotype.Service;

@Service
public class LoginService {

	public boolean login(String login, String password) {
		
		if ( "admin".equals( login ) ) { return true; }
		
		return false;
	}
	
	
	
	

}
