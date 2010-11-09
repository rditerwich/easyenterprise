package easyenterprise.server.party;

import junit.framework.Assert;

import org.junit.Test;

import easyenterprise.lib.command.CommandException;
import easyenterprise.server.party.command.FindParties;
import easyenterprise.server.party.command.FindPartiesResult;
import easyenterprise.server.party.command.UpdateOrganization;
import easyenterprise.server.party.command.UpdatePartyResult;
import easyenterprise.server.party.command.UpdatePerson;
import easyenterprise.server.party.entity.Organization;
import easyenterprise.server.party.entity.Person;

public class PartyTest extends TestBase {

	@Test
	public void testCreatePerson() throws CommandException {
		
		UpdatePerson command = new UpdatePerson()
			.setParty(new Person()
				.setFirstName("John")
				.setLastName("Smith"));
		
		UpdatePartyResult<Person> result = getServer().execute(command);
		
		System.out.println("Created person " + result.getParty().getId() + ": " + result.getParty().getFirstName());
		Assert.assertNotSame(command.getParty(), result.getParty());
		Assert.assertNotNull(result.getParty().getId());
	}
	
	@Test
	public void testFindParties() throws CommandException {
		getServer().execute(new UpdatePerson()
			.setParty(new Person()
				.setFirstName("Mary")
				.setLastName("Poppins")));
		getServer().execute(new UpdateOrganization()
			.setParty(new Organization()
				.setName("The Money Pit Ltd.")));
		
		FindParties command = new FindParties();
		FindPartiesResult result = getServer().execute(command);
		System.out.println(result.getParties());
		Assert.assertEquals(3, result.getParties().size());
	}
}
