package de.greyshine.vuespringexample.email.entitys;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.greyshine.vuespringexample.email.entitys.EmailBinary.Type;
import de.greyshine.vuespringexample.utils.Utils;

@Entity
@Table(name = "emails")
public class EmailEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;
	
	private LocalDateTime created;
	private LocalDateTime updated;
	
	private LocalDateTime send;
	private LocalDateTime failed;
	
	private int sendFailures;
	
	@NotNull
	private String tos;
	private String subject;
	
	@Lob
	private String html;
	
	@Lob
	private String plaintext;
	
	@OneToMany( cascade=CascadeType.PERSIST )
	@JoinColumn( name = "fk_email" )
	private List<EmailBinary> binarydatas = new ArrayList<>();
	
	public LocalDateTime getUpdated() {
		return updated;
	}
	
	public LocalDateTime getSend() {
		return send;
	}

	public void setSend(LocalDateTime send) {
		this.send = send;
	}

	public LocalDateTime getFailed() {
		return failed;
	}

	public void setFailed(LocalDateTime failed) {
		this.failed = failed;
	}

	public int getSendFailures() {
		return sendFailures;
	}

	public int addSendFailure() {
		return sendFailures += 1;
	}

	public String getTos() {
		return tos;
	}

	public void setTos(String tos) {
		this.tos = tos;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = Utils.trimToNull( subject );
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = Utils.trimToNull( html );
	}
	
	public boolean isHtmlAvailable() {
		return Utils.isNotBlank( html );
	}

	public String getPlaintext() {
		return plaintext;
	}

	public void setPlaintext(String plaintext) {
		this.plaintext = Utils.trimToNull( plaintext );
	}
	
	public boolean isPlaintextAvailable() {
		return Utils.isNotBlank( plaintext );
	}
	
	public EmailEntity addData(EmailBinary binaryData) {
		if ( binaryData == null || binarydatas.contains( binaryData ) ) { return this; }
		this.binarydatas.add( binaryData );
		return this;
	}
	
	public List<EmailBinary> getDatas(Type type) {
		
		final List<EmailBinary> datas = new ArrayList<>();
		
		for (EmailBinary emailBinary : this.binarydatas) {
			if ( emailBinary != null && emailBinary.type == type ) {
				datas.add( emailBinary );
			}
		}
		
		return datas;
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
		return Objects.hash( id );
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof EmailEntity) ) { return false; }
		
		return Utils.equals(this.id, ((EmailEntity)obj).id);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() +" [id="+id+", created="+ Utils.formatDate( Utils.LOCALDATETIME_FORMAT , created) +", to="+ tos +", subject="+ (subject==null?"":subject) +", failed="+ Utils.formatDate(Utils.LOCALDATETIME_FORMAT, failed) +", sendFailures="+ sendFailures +"]";
	}
}
