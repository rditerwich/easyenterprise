package easyenterprise.server.party.commands;

import easyenterprise.lib.command.Command;
import easyenterprise.server.party.Party;

public class UpdateParty<T extends Party> implements Command<UpdatePartyResult<T>> {

	private static final long serialVersionUID = 1L;

	/**
	 * Create when 
	 */
	public T party;
	
}
