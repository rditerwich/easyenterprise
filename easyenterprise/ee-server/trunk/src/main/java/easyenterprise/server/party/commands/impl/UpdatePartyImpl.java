package easyenterprise.server.party.commands.impl;

import easyenterprise.lib.command.jpa.TransactionalCommandImpl;
import easyenterprise.server.party.Party;
import easyenterprise.server.party.commands.UpdateParty;
import easyenterprise.server.party.commands.UpdatePartyResult;

public abstract class UpdatePartyImpl<T extends Party, U extends UpdateParty<T>> extends TransactionalCommandImpl<UpdatePartyResult<T>, U> {

	protected UpdatePartyResult<T> updateParty(T party) {
		UpdatePartyResult<T> result = new UpdatePartyResult<T>();
		result.party = party;
		return result;
	}
}
