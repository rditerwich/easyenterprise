package easyenterprise.server.party.commands.impl;

import easyenterprise.lib.command.CommandException;
import easyenterprise.server.party.Organization;
import easyenterprise.server.party.commands.UpdateOrganization;
import easyenterprise.server.party.commands.UpdatePartyResult;

public class UpdateOrganizationImpl extends UpdatePartyImpl<Organization, UpdateOrganization> {

	public UpdatePartyResult<Organization> execute(UpdateOrganization command) throws CommandException {
		
		// create a new party?
		Organization organization;
		if (command.party.getId() != null) {
			organization = getEntityManager().find(Organization.class, command.party.getId());
		} else {
			organization = new Organization();
		}
		
		// copy person fields
		organization.setName(command.party.getName());
		
		// copy party fields
		return updateParty(organization);
	}

}
