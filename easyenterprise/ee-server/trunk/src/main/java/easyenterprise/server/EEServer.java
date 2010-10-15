package easyenterprise.server;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;

import org.eclipse.persistence.Version;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandService;
import easyenterprise.lib.command.RegisteredCommands;
import easyenterprise.lib.command.jpa.JpaCommandWrapper;
import easyenterprise.server.account.command.impl.RegisteredAccountCommands;

public class EEServer implements CommandService {

	private final RegisteredCommands registeredCommands = RegisteredCommands.create(new RegisteredAccountCommands());

	private final EntityManagerFactory entityManagerFactory;
	private final JpaCommandWrapper jpaCommandWrapper;
	
	public EEServer(ServletConfig config) {
		this(collectProperties(config));
	}

	public EEServer(Map<String, String> properties) {
		entityManagerFactory = Persistence.createEntityManagerFactory("ee-database", properties);
		jpaCommandWrapper = new JpaCommandWrapper(registeredCommands, entityManagerFactory );
	}
	
	@Override
	public <T extends CommandResult, C extends Command<T>> T execute(C command) throws CommandException {
		return jpaCommandWrapper.execute(command);
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
