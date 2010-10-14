package easyenterprise.server.party.commands;

import easyenterprise.lib.command.CommandResult;
import easyenterprise.server.party.Party;

public class UpdatePartyResult<T extends Party> implements CommandResult {

	private static final long serialVersionUID = 1L;
	
	public T party;

}
