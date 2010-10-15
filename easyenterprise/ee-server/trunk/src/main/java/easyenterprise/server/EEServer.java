package easyenterprise.server;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandService;
import easyenterprise.lib.command.RegisteredCommands;
import easyenterprise.lib.command.jpa.JpaCommandWrapper;
import easyenterprise.server.account.impl.RegisteredAccountCommands;

public class EEServer implements CommandService {

	private final RegisteredCommands registeredCommands = RegisteredCommands.create(new RegisteredAccountCommands());

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ee-persistenceunit");
	private final JpaCommandWrapper jpaCommandWrapper = new JpaCommandWrapper(registeredCommands, entityManagerFactory );
	
	@Override
	public <T extends CommandResult, C extends Command<T>> T execute(C command) throws CommandException {
		return jpaCommandWrapper.execute(command);
	}
}
