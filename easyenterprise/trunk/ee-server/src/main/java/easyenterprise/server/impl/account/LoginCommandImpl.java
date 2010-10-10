package easyenterprise.server.impl.account;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.server.account.LoginCommand;
import easyenterprise.server.account.LoginCommandResult;

public class LoginCommandImpl implements CommandImpl<LoginCommandResult, LoginCommand> {

	public Class<LoginCommand> getActionType() {
		return LoginCommand.class;
	}

	public LoginCommandResult execute(LoginCommand command) throws CommandException {
		LoginCommandResult result = new LoginCommandResult();
		result.message = "Hello " + command.loginName + ", you haven been logged in!";
		return result;
	}

}
