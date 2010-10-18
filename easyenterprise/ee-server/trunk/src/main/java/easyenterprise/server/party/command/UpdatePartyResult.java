package easyenterprise.server.party.command;

import easyenterprise.lib.command.CommandResult;
import easyenterprise.server.party.entity.Party;

public class UpdatePartyResult<T extends Party<T>> implements CommandResult {

	private static final long serialVersionUID = 1L;
	
	private T party;

	public T getParty() {
		return party;
	}
	
	public UpdatePartyResult<T> setParty(T party) {
		this.party = party;
		return this;
	}
}
