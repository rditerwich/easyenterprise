package easyenterprise.server.party.command;

import easyenterprise.lib.command.Command;
import easyenterprise.server.party.entity.Party;

public class UpdateParty<T extends Party<T>, This extends UpdateParty<T, This>> implements Command<UpdatePartyResult<T>> {

	private static final long serialVersionUID = 1L;

	/**
	 * Create when 
	 */
	private T party;

	public T getParty() {
		return party;
	}
	
	@SuppressWarnings("unchecked")
	public This setParty(T party) {
		this.party = party;
		return (This) this;
	}
	
}
