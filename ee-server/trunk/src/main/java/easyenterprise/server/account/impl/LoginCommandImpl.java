package easyenterprise.server.account.impl;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.server.account.command.LoginCommand;
import easyenterprise.server.account.command.LoginCommandResult;

public class LoginCommandImpl extends LoginCommand implements CommandImpl<LoginCommandResult> {

  private static final long serialVersionUID = 1L;

	public Class<LoginCommand> getActionType() {
		return LoginCommand.class;
	}

	public LoginCommandResult execute() throws CommandException {
		LoginCommandResult result = new LoginCommandResult();
		result.message = "Hello " + loginName + ", you haven been logged in!";
		return result;
	}

}
