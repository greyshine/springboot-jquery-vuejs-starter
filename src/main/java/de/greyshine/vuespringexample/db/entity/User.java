package de.greyshine.vuespringexample.db.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.sun.istack.NotNull;

import de.greyshine.vuespringexample.utils.Utils;

@Entity
//quotations are important due to naming conflict w/ reserved postgres' user term.
@Table(name = "\"user\"")
public class User {

	@Id
	private String login;

	@Version
	@Column( name="_version" )
	private int version;

	@Column( name="_created" )
	private LocalDateTime created;
	@SuppressWarnings("unused")
	@Column( name="_updated" )
	private LocalDateTime updated;
	
	@ElementCollection
    @CollectionTable(name = "user_rights", joinColumns = @JoinColumn(name = "fk_user"))
    @Column(name = "\"right\"")
	private List<String> rights = new ArrayList<>();
	
	private boolean active = false;

	@NotNull
	private String password;

	private int failedLogins = 0;
	
	private String sessionToken;
	
	@NotBlank
	// regex taken from: https://emailregex.com/
	@Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
	private String email;

	public LocalDateTime getCreated() {
		return created;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public List<String> getRights() {
		return rights;
	}
	
	public void setRights( List<String> rights ) {
		this.rights = rights;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getFailedLogins() {
		return failedLogins;
	}

	public void setFailedLogins(int failedLogins) {
		this.failedLogins = failedLogins;
	}

	@PrePersist
	public void prePersist() {
		
		created = LocalDateTime.now();
		updated = created;
	}

	@PreUpdate
	public void preUpdate() {
		updated = LocalDateTime.now();
	}
	
	public void increaseBadPasswordCount() {
		failedLogins++;
	}
	
	public void resetBadPasswordCount() {
		failedLogins = 0;
	}
	
	public User addRight(String right) {
		
		right = Utils.trimToNull( right );
		
		if ( Utils.isBlank( right ) ) { return this; }
		
		
		for(String aRight : rights) {
			if ( right.equalsIgnoreCase( aRight ) ) { return this; }
		}
		
		rights.add( right );
		return this;
	}
	
	public String getSessionToken() {
		return sessionToken;
	}
	
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + failedLogins;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (active != other.active)
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (failedLogins != other.failedLogins)
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return User.class.getSimpleName() + " [login=" + login + ", created="+ Utils.toString( created ) +"]";
	}
	
}