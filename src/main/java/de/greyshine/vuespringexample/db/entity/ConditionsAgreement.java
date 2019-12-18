package de.greyshine.vuespringexample.db.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.greyshine.vuespringexample.utils.Utils;

@Entity
@Table( name = "conditions_agreements" )
public class ConditionsAgreement implements Serializable {
	
	private static final long serialVersionUID = 840878006667359380L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column( nullable = false )
	private LocalDateTime validFrom;
	
	@Column( nullable = false )
	private String context;
	
	@Column( name="_created" )
	private LocalDateTime created;
	@Column( name="_updated" )
	private LocalDateTime updated;
	
	@Lob
	@NotNull
	private String text;
	
	public Long getId() {
		return id;
	}
	
	public LocalDateTime getValidFrom() {
		return validFrom;
	}
	
	public void setValidFrom(LocalDateTime validFrom) {
		this.validFrom = validFrom;
	}
	
	public String getContext() {
		return context;
	}
	
	public void setContext(String context) {
		this.context = context == null || context.strip().isEmpty() ? null : context.strip();
	}
	
	public void setText(String text) {
		this.text = text == null || text.strip().isEmpty() ? null : text.strip();
	}
	
	public String getText() {
		return text;
	}
	
	@PrePersist
	public void prePersists() {
		created = LocalDateTime.now();
		updated = created;
	}
	
	@PreUpdate
	public void preUpdate() {
		updated = LocalDateTime.now();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash( id, validFrom, context, text );
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if ( obj == this ) { return true; }
		if ( obj == null ) { return false; }
		
		final ConditionsAgreement ca = (ConditionsAgreement)obj;
		return Utils.equals( id, ca.id ) && Utils.equals( context , ca.context, false) && Utils.equals( validFrom , ca.validFrom) ;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+" [id="+ id +", created="+ Utils.formatDate( created ) +", updated="+ Utils.formatDate( updated ) +", validFrom="+ validFrom +", context="+ context +", text.length="+ (text==null?-1:text.length()) +"]";
	}
}
