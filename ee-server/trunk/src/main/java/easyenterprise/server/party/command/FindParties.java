package easyenterprise.server.party.command;

import easyenterprise.lib.command.Command;
import easyenterprise.server.party.entity.Organization;

public class FindParties implements Command<FindPartiesResult> {

	private static final long serialVersionUID = 1L;

	private Organization organization;
	
	public Organization getOrganization() {
		return organization;
	}
	
	public FindParties setOrganization(Organization organization) {
		this.organization = organization;
		return this;
	}
	
}
