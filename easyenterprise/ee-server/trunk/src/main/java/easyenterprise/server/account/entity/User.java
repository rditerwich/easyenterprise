package easyenterprise.server.account.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class User {

	@ManyToOne
	@JoinColumn(nullable=false)
	private Account account;
	
	private String loginName;
	private String encryptedPassword;
}
