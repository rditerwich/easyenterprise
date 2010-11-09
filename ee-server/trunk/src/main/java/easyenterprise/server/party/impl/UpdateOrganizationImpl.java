package easyenterprise.server.party.impl;

import static easyenterprise.lib.command.jpa.JpaService.getEntityManager;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandService;
import easyenterprise.server.party.command.UpdateOrganization;
import easyenterprise.server.party.command.UpdatePartyResult;
import easyenterprise.server.party.entity.Organization;

public class UpdateOrganizationImpl extends UpdateOrganization implements CommandImpl<UpdatePartyResult<Organization>> {

  private static final long serialVersionUID = 1L;
	private static final View view = new BasicView("relations");
	
	public UpdatePartyResult<Organization> execute() throws CommandException {
		
		// create a new organization?
		Organization organization;
		if (getParty().getId() != null) {
			organization = getEntityManager().find(Organization.class, getParty().getId());
		} else {
			organization = new Organization();
			getEntityManager().persist(organization);
		}
		
		// copy organization fields
		organization.setName(getParty().getName());
		
		// copy party fields
		return new UpdatePartyResult<Organization>()
			.setParty(CommandService.clone(organization, view));
	}
}