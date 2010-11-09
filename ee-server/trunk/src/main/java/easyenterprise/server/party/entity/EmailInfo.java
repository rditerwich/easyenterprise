package easyenterprise.server.party.entity;

import javax.persistence.Column;


public class EmailInfo extends PartyInfo {
	
	private static final long serialVersionUID = 1L;
	
	@Column(nullable=false) 
	private String email = "";

	public String getEmail() {
		return email;
	}
	
	public EmailInfo setEmail(String email) {
		this.email = email;
		return this;
	}
}
