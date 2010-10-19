package easyenterprise.server.party.command.impl;

import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.jpa.TransactionalCommandImpl;
import easyenterprise.server.party.command.UpdateParty;
import easyenterprise.server.party.command.UpdatePartyResult;
import easyenterprise.server.party.entity.Party;

public abstract class UpdatePartyImpl<T extends Party<T>, U extends UpdateParty<T, U>> extends TransactionalCommandImpl<UpdatePartyResult<T>, U> {

	@Override
	public View getView() {
		return new BasicView("party/relations");
	}
	
	protected UpdatePartyResult<T> updateParty(T party) {
		return new UpdatePartyResult<T>()
			.setParty(party);
	}
}
