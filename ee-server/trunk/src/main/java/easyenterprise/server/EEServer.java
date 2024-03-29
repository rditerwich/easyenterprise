package easyenterprise.server;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandServer;
import easyenterprise.lib.command.CommandExecutor;
import easyenterprise.lib.command.RegisteredCommands;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.server.account.impl.RegisteredAccountCommands;
import easyenterprise.server.party.impl.RegisteredPartyCommands;

public class EEServer implements CommandExecutor {

	private final RegisteredCommands registeredCommands = RegisteredCommands.create(
			new RegisteredAccountCommands(), 
			new RegisteredPartyCommands());

	private final EntityManagerFactory entityManagerFactory;
	private final CommandExecutor executor;
	
	public EEServer(ServletConfig config) {
		this(collectProperties(config));
	}

	public EEServer(Map<String, String> properties) {
		entityManagerFactory = Persistence.createEntityManagerFactory("ee-database", properties);
		CommandServer server = new CommandServer(registeredCommands);
		executor = new JpaService(server, entityManagerFactory);
	}
	
	@Override
	public <T extends CommandResult, C extends Command<T>> T execute(C command) throws CommandException {
		return executor.execute(command);
	}
	
	private static Map<String, String> collectProperties(ServletConfig config) {
		Map<String, String> properties = new HashMap<String, String>();
		
		for (@SuppressWarnings("unchecked")
		Enumeration<String> e = config.getInitParameterNames(); e.hasMoreElements(); ) {
			String name = e.nextElement();
			properties.put(name, config.getInitParameter(name));
		}
		properties.putAll(System.getenv());
		for (String name : System.getProperties().stringPropertyNames()) {
			properties.put(name, System.getProperty(name));
		}
		return properties;
	}
}
