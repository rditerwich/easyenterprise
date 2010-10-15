package easyenterprise.server.party.command.impl;

import easyenterprise.lib.command.CommandException;
import easyenterprise.server.party.command.UpdateOrganization;
import easyenterprise.server.party.command.UpdatePartyResult;
import easyenterprise.server.party.entity.Organization;

public class UpdateOrganizationImpl extends UpdatePartyImpl<Organization, UpdateOrganization> {

	public UpdatePartyResult<Organization> execute(UpdateOrganization command) throws CommandException {
		
		// create a new party?
		Organization organization;
		if (command.getParty().getId() != null) {
			organization = getEntityManager().find(Organization.class, command.getParty().getId());
		} else {
			organization = new Organization();
		}
		
		// copy person fields
		organization.setName(command.getParty().getName());
		
		// copy party fields
		return updateParty(organization);
	}

}
