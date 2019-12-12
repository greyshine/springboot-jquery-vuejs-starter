package de.greyshine.vuespringexample.db.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import de.greyshine.vuespringexample.utils.Utils;

@Entity
@Table( name="weblogs" )
public class Weblog {

	@Id
	private String id;
	
	@Column( name="\"user\"" )
	private String user;
	
	private LocalDateTime timestamp;
	
	private Integer durance;
	
	private String ip;
	
	private String uri;
	
	private String params;
	
	private String latlon;
	
	private String exception;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = Utils.trimToNull(user);
	}

	public void setDurance(Integer durance) {
		this.durance = durance;
	}
	
	public Integer getDurance() {
		return durance;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public void setParams(String params) {
		this.params = params;
	}
	
	public String getParams() {
		return params;
	}
	
	public void setLatlon(String latlon) {
		this.latlon = latlon;
	}
	
	public String getLatlon() {
		return latlon;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
	
	public String getException() {
		return exception;
	}
	
	@Override
	public String toString() {
		return Weblog.class.getSimpleName()+" [id="+ id +", timestamp="+ Utils.formatDate( Utils.LOCALDATETIME_FORMAT , timestamp) +", user="+user+", durance="+ durance +"ms]";
	}
}
