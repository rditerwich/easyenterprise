package easyenterprise.server.account.command.impl;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredAccountCommands extends RegisteredCommands {

	@Override
	protected void register() throws CommandException {
		register(new LoginCommandImpl());
	}

}
