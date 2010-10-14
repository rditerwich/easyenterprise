package easyenterprise.server.party.commands.impl;

import easyenterprise.lib.command.CommandException;
import easyenterprise.server.party.Person;
import easyenterprise.server.party.commands.UpdatePartyResult;
import easyenterprise.server.party.commands.UpdatePerson;

public class UpdatePersonImpl extends UpdatePartyImpl<Person, UpdatePerson> {

	public UpdatePartyResult<Person> execute(UpdatePerson command) throws CommandException {
		
		// create a new party?
		Person person;
		if (command.party.getId() != null) {
			person = getEntityManager().find(Person.class, command.party.getId());
		} else {
			person = new Person();
		}
		
		// copy person fields
		person.setFirstName(command.party.getFirstName());
		person.setMiddleName(command.party.getMiddleName());
		person.setLastName(command.party.getLastName());
		
		// copy party fields
		return updateParty(person);
	}

}
