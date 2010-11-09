package easyenterprise.server.account.impl;

import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredAccountCommands extends RegisteredCommands {

	public RegisteredAccountCommands() {
		register(LoginCommandImpl.class);
	}

}
