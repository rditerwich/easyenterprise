package easyenterprise.server.gwt;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.RegisteredCommands;
import easyenterprise.lib.command.gwt.GwtCommandService;
import easyenterprise.lib.command.jpa.JpaCommandWrapper;
import easyenterprise.server.account.impl.RegisteredAccountCommands;

public class EasyEnterpriseServlet extends RemoteServiceServlet implements GwtCommandService {

	private static final long serialVersionUID = 1L;

	private final RegisteredCommands registeredCommands = RegisteredCommands.create(new RegisteredAccountCommands());

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ee-persistenceunit");
	private final JpaCommandWrapper jpaCommandWrapper = new JpaCommandWrapper(registeredCommands, entityManagerFactory );
	
	@Override
	public <T extends CommandResult> T execute(Command<T> command) throws CommandException {
		return jpaCommandWrapper.execute(command);
	}
	
}
