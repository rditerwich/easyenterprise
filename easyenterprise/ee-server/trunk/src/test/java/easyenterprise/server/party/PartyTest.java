package easyenterprise.server.party;

import junit.framework.Assert;

import org.junit.Test;

import easyenterprise.lib.command.CommandException;
import easyenterprise.server.party.command.UpdatePartyResult;
import easyenterprise.server.party.command.UpdatePerson;
import easyenterprise.server.party.entity.Person;

public class PartyTest extends TestBase {

	@Test
	public void testCreatePerson() throws CommandException {
		
		UpdatePerson command = new UpdatePerson()
			.setParty(new Person()
				.setFirstName("John")
				.setLastName("Smith"));
		
		UpdatePartyResult<Person> result = getServer().execute(command);
		
		Assert.assertNotSame(command.getParty(), result.getParty());
		Assert.assertNotNull(result.getParty().getId());
		System.out.println("Created person " + result.getParty().getId() + ": " + result.getParty().getFirstName());
	}
}
