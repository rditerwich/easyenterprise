package easyenterprise.server.party.impl;

import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredPartyCommands extends RegisteredCommands {

	public RegisteredPartyCommands() {
		register(FindPartiesImpl.class);
		register(UpdatePersonImpl.class);
		register(UpdateOrganizationImpl.class);
	}

}
