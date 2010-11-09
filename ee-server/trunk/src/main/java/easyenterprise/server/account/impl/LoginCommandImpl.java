package easyenterprise.server.account.impl;

import easyenterprise.lib.command.AbstractCommandImpl;
import easyenterprise.lib.command.CommandException;
import easyenterprise.server.account.command.LoginCommand;
import easyenterprise.server.account.command.LoginCommandResult;

public class LoginCommandImpl extends AbstractCommandImpl<LoginCommandResult, LoginCommand> {

	public Class<LoginCommand> getActionType() {
		return LoginCommand.class;
	}

	public LoginCommandResult execute(LoginCommand command) throws CommandException {
		LoginCommandResult result = new LoginCommandResult();
		result.message = "Hello " + command.loginName + ", you haven been logged in!";
		return result;
	}

}
