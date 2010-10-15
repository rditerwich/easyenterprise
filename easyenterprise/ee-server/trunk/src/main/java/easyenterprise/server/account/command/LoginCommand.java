package easyenterprise.server.account.command;

import easyenterprise.lib.command.Command;

public class LoginCommand implements Command<LoginCommandResult> {

	private static final long serialVersionUID = 1L;

	/**
	 * Used to determine the account. Field is only considered when 
	 * {@link #account} field is null.
	 */
	public String url;
	public String account;
	public String loginName;
	public String encryptedPassword;
}
