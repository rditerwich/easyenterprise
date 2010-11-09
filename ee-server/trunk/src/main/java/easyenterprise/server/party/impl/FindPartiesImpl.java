package easyenterprise.server.party.impl;

import static easyenterprise.lib.command.jpa.JpaService.getEntityManager;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandService;
import easyenterprise.server.party.command.FindParties;
import easyenterprise.server.party.command.FindPartiesResult;
import easyenterprise.server.party.entity.Party;

public class FindPartiesImpl extends FindParties implements CommandImpl<FindPartiesResult> {

  private static final long serialVersionUID = 1L;
	private static final View view = new BasicView("invoiceAddress");
	
  @Override
  @SuppressWarnings("rawtypes")
	public FindPartiesResult execute() throws CommandException {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Party> query = builder.createQuery(Party.class);
		List<Party> parties = getEntityManager().createQuery(query).getResultList();
		return new FindPartiesResult().setParties(CommandService.clone(parties, view));
	}
}
