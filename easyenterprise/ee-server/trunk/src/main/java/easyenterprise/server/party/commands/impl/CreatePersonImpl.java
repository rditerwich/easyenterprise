package easyenterprise.server.party.commands.impl;

import javax.persistence.EntityManager;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.jpa.TransactionalCommandImpl;
import easyenterprise.server.party.Party;
import easyenterprise.server.party.commands.UpdateParty;
import easyenterprise.server.party.commands.UpdatePartyResult;

public class CreatePersonImpl extends TransactionalCommandImpl<UpdatePartyResult, UpdateParty> {

	public UpdatePartyResult execute(UpdateParty command) throws CommandException {
		
		// create a new party?
		Party existingParty;
		if (command.party.getId() != null) {
			existingParty = getEntityManager().find(command.party.getClass(), command.party.getId());
		} else {
			existingParty = new Party()
			em.persist(command.party);
		}
	}

}
