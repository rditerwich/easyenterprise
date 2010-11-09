package easyenterprise.server.party.impl;

import static easyenterprise.lib.command.jpa.JpaService.getEntityManager;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandService;
import easyenterprise.server.party.command.UpdatePartyResult;
import easyenterprise.server.party.command.UpdatePerson;
import easyenterprise.server.party.entity.Person;

public class UpdatePersonImpl extends UpdatePerson implements CommandImpl<UpdatePartyResult<Person>> {

  private static final long serialVersionUID = 1L;
	private static final View view = new BasicView("relations");
	
	public UpdatePartyResult<Person> execute() throws CommandException {
		
		// create a new party?
		Person person;
		if (getParty().getId() != null) {
			person = getEntityManager().find(Person.class, getParty().getId());
		} else {
			person = new Person();
			getEntityManager().persist(person);
		}
		
		// copy person fields
		person.setFirstName(getParty().getFirstName());
		person.setMiddleName(getParty().getMiddleName());
		person.setLastName(getParty().getLastName());
		
		// copy party fields
		return new UpdatePartyResult<Person>()
			.setParty(CommandService.clone(person, view));
	}
}
