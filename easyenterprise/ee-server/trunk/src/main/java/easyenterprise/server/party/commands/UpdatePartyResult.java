package easyenterprise.server.party.commands;

import easyenterprise.lib.command.CommandResult;
import easyenterprise.server.party.Party;

public class UpdatePartyResult<T extends Party<T>> implements CommandResult {

	private static final long serialVersionUID = 1L;
	
	public T party;

	
	public UpdatePartyResult<T> setParty(T party) {
		this.party = party;
		return this;
	}
}
