package de.greyshine.vuespringexample.db.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import de.greyshine.vuespringexample.utils.Utils;

@Entity
@Table( name = "user_conditions_agreements" )
@IdClass( UserConditionsAgreement.Pk.class )
public class UserConditionsAgreement {

	@Id
	@Column( name="user_login", nullable = false )
	private String login;
	
	@Id
	private long conditionsAgreementId;
	
	@Column( name="_created" )
	private LocalDateTime created;
	
	@Column( name="_updated" )
	private LocalDateTime updated;
	
	public String geLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public long getConditionsAgreementId() {
		return conditionsAgreementId;
	}
	
	public void setConditionsAgreementId(long conditionsAgreementId) {
		this.conditionsAgreementId = conditionsAgreementId;
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
	
	@Override
	public int hashCode() {
		return Objects.hash( login, conditionsAgreementId );
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		
		final UserConditionsAgreement uca = (UserConditionsAgreement)obj;
		return Utils.equals( this.login, uca.login) && Utils.equals( this.conditionsAgreementId,  uca.conditionsAgreementId ); 
	}
	
	@Override
	public String toString() { 
		return getClass().getSimpleName()+" [user.login="+ login +", conditionsAgreement.id="+ conditionsAgreementId +"]";
	}
	
	
	public static class Pk implements Serializable {

		private static final long serialVersionUID = -844333972505315688L;
		
		private String login;
		private Long conditionsAgreementId;
		
		public Pk() {}
		public Pk(String login, Long conditionsAgreementId) {
			this.login = login;
			this.conditionsAgreementId = conditionsAgreementId;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash( login, conditionsAgreementId );
		}
		
		@Override
		public boolean equals(Object obj) {
			
			if ( obj == this ) { return true; }
			if ( obj == null ) { return false; }
			
			final Pk oPk = (Pk)obj;
			return Utils.equals( login, oPk.login) && Utils.equals( conditionsAgreementId, oPk.conditionsAgreementId );
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+"(login="+ login +", conditionsAgreement="+ conditionsAgreementId +")";
		}
	}
}
