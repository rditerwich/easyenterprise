package easyenterprise.server.party.command.impl;

import easyenterprise.lib.command.jpa.TransactionalCommandImpl;
import easyenterprise.server.party.Party;
import easyenterprise.server.party.command.UpdateParty;
import easyenterprise.server.party.command.UpdatePartyResult;

public abstract class UpdatePartyImpl<T extends Party<T>, U extends UpdateParty<T, U>> extends TransactionalCommandImpl<UpdatePartyResult<T>, U> {

	protected UpdatePartyResult<T> updateParty(T party) {
		UpdatePartyResult<T> result = new UpdatePartyResult<T>();
		result.party = party;
		return result;
	}
}
