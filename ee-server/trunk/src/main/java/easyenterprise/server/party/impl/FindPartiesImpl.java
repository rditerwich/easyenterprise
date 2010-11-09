package easyenterprise.server.party.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.jpa.TransactionalCommandImpl;
import easyenterprise.server.party.command.FindParties;
import easyenterprise.server.party.command.FindPartiesResult;
import easyenterprise.server.party.entity.Party;

public class FindPartiesImpl extends TransactionalCommandImpl<FindPartiesResult, FindParties>{

	@Override
	public View getView() {
		return new BasicView("parties");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public FindPartiesResult execute(FindParties command) throws CommandException {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Party> query = builder.createQuery(Party.class);
		return new FindPartiesResult().setParties(getEntityManager().createQuery(query).getResultList());
	}

}
