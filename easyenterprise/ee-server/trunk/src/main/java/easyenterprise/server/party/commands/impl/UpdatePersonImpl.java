package easyenterprise.server.party.commands.impl;

import easyenterprise.lib.command.CommandException;
import easyenterprise.server.party.Person;
import easyenterprise.server.party.commands.UpdatePartyResult;
import easyenterprise.server.party.commands.UpdatePerson;

public class UpdatePersonImpl extends UpdatePartyImpl<Person, UpdatePerson> {

	public UpdatePartyResult<Person> execute(UpdatePerson command) throws CommandException {
		
		// create a new party?
		Person person;
		if (command.getParty().getId() != null) {
			person = getEntityManager().find(Person.class, command.getParty().getId());
		} else {
			person = new Person();
		}
		
		// copy person fields
		person.setFirstName(command.getParty().getFirstName());
		person.setMiddleName(command.getParty().getMiddleName());
		person.setLastName(command.getParty().getLastName());
		
		// copy party fields
		return updateParty(person);
	}

}
