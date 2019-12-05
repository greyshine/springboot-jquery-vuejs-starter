package de.greyshine.vuespringexample.email.entitys;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.greyshine.vuespringexample.utils.Utils;

@Entity
@Table(name = "emailbinarys")
public class EmailBinary {
	
	public enum Type {
		ATTACHMENT, INLINE;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@NotNull
	public String name;
	
	@NotNull
	public String contentType;
	
	@NotNull
	@Enumerated( EnumType.STRING )
	public Type type;
	
	@Lob
	@NotNull
	public byte[] data;

	@Override
	public int hashCode() {
		return Objects.hash( id, type, name, data );
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this==obj ) { return true; }
		if ( obj==null ) { return false; }
		return Utils.equals( id , ((EmailBinary)obj).id ) ;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+" [id="+ id +", name="+ name +", contentType="+ contentType +", data.length="+ (data==null?-1:data.length) +"]";
	}
}
