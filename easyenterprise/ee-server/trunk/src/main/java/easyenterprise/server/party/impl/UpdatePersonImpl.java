package easyenterprise.server.party.impl;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.jpa.JpaCommandService;
import easyenterprise.server.party.command.UpdatePartyResult;
import easyenterprise.server.party.command.UpdatePerson;
import easyenterprise.server.party.entity.Person;

public class UpdatePersonImpl extends UpdatePartyImpl<Person, UpdatePerson> {

	public UpdatePartyResult<Person> execute(UpdatePerson command) throws CommandException {
		
		// create a new party?
		Person person;
		if (command.getParty().getId() != null) {
			person = JpaCommandService.getEntityManager().find(Person.class, command.getParty().getId());
		} else {
			person = new Person();
			JpaCommandService.getEntityManager().persist(person);
		}
		
		// copy person fields
		person.setFirstName(command.getParty().getFirstName());
		person.setMiddleName(command.getParty().getMiddleName());
		person.setLastName(command.getParty().getLastName());
		
		// copy party fields
		return updateParty(person);
	}

}
