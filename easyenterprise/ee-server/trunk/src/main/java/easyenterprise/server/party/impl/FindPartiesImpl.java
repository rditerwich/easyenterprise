package easyenterprise.server.party.impl;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.jpa.JpaCommandService;
import easyenterprise.lib.command.jpa.TransactionalCommandImpl;
import easyenterprise.server.party.command.FindParties;
import easyenterprise.server.party.command.FindPartiesResult;

public class FindPartiesImpl extends TransactionalCommandImpl<FindPartiesResult, FindParties>{

	@Override
	public FindPartiesResult execute(FindParties command) throws CommandException {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
//		builder.
//		entityManager.createQuery(Party.class, )
		return null;
	}

}
