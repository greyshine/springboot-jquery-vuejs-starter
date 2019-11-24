package de.greyshine.vuespringexample.db.repos;

import org.springframework.data.repository.CrudRepository;

import de.greyshine.vuespringexample.db.entity.User;

public interface UserRepository extends CrudRepository<User, String> {

	User findByLogin(String login);
	
	User findBySessionToken(String sessionToken);
	
}
