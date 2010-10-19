package easyenterprise.server.account.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class User {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Account account;
	
	private String loginName;
	private String encryptedPassword;
	
	public Account getAccount() {
		return account;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public String getLoginName() {
		return loginName;
	}
	
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}
	
	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}
}
