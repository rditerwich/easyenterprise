package easyenterprise.server.party.command;

import java.util.List;

import easyenterprise.lib.command.CommandResult;
import easyenterprise.server.party.entity.Party;

public class FindPartiesResult implements CommandResult {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	private List<? extends Party> parties;
	
	@SuppressWarnings("unchecked")
	public List<? extends Party> getParties() {
		return parties;
	}
	
	@SuppressWarnings("unchecked")
	public FindPartiesResult setParties(List<? extends Party> parties) {
		this.parties = parties;
		return this;
	}
}
