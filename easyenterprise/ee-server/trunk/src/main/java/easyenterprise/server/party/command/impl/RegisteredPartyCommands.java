package easyenterprise.server.party.command.impl;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredPartyCommands extends RegisteredCommands {

	@Override
	protected void register() throws CommandException {
		register(new UpdatePersonImpl());
		register(new UpdateOrganizationImpl());
	}

}
