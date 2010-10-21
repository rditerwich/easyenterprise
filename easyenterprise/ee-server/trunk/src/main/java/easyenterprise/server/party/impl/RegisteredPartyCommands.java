package easyenterprise.server.party.impl;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredPartyCommands extends RegisteredCommands {

	@Override
	protected void register() throws CommandException {
		register(FindPartiesImpl.class);
		register(UpdatePersonImpl.class);
		register(UpdateOrganizationImpl.class);
	}

}
