package easyenterprise.server.party.impl;

import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandService;
import easyenterprise.server.party.command.UpdateParty;
import easyenterprise.server.party.command.UpdatePartyResult;
import easyenterprise.server.party.entity.Party;

public abstract class UpdatePartyImpl<T extends Party<T>, U extends UpdateParty<T, U>> extends UpdateParty<T, U> implements CommandImpl<UpdatePartyResult<T>> {

  private static final long serialVersionUID = 1L;
  private static final View view = new BasicView("relations");
	
	protected UpdatePartyResult<T> updateParty(T party) throws CommandException {
		return new UpdatePartyResult<T>()
			.setParty(CommandService.clone(party, view));
	}
}
