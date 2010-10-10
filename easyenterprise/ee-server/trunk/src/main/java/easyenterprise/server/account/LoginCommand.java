package easyenterprise.server.account;

import easyenterprise.lib.command.Command;

public class LoginCommand implements Command<LoginCommandResult> {

	private static final long serialVersionUID = 1L;

	public String loginName;
	public String encryptedPassword;
}
