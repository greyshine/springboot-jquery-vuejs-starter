package de.greyshine.vuespringexample.web.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.greyshine.vuespringexample.annotations.Access;
import de.greyshine.vuespringexample.db.repos.UserRepository;
import de.greyshine.vuespringexample.utils.Utils;

@RestController
@RequestMapping("/rest")
public class UsersController {
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping( "/users" )
	@Access( "right1" )
	public Result<User> users() {
		
		final List<User> users = new ArrayList<>();
		userRepository.findAll().forEach( u -> users.add( convert( u ) ) );
		
		return new Result<>( users );
	}
	
	private User convert(de.greyshine.vuespringexample.db.entity.User userEntity) {
		
		final User user = new User();
		user.login = userEntity.getLogin();
		user.created = Utils.toString( userEntity.getCreated() );
		user.active = userEntity.isActive();
		user.loginfails = userEntity.getFailedLogins();
		
		return user;
	}
	
	public static class User {
		
		public String login;
		public String created;
		public boolean active;
		public int loginfails;
	}

}
