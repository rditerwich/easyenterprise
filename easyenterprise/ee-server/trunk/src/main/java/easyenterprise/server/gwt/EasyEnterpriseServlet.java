package easyenterprise.server.gwt;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.RegisteredCommands;
import easyenterprise.lib.command.gwt.GwtCommandService;
import easyenterprise.server.account.impl.RegisteredAccountCommands;

public class EasyEnterpriseServlet extends RemoteServiceServlet implements GwtCommandService {

	private static final long serialVersionUID = 1L;

	private final RegisteredCommands registeredCommands = RegisteredCommands.create(new RegisteredAccountCommands());

	@Override
	public <T extends CommandResult> T execute(Command<T> command) throws CommandException {
		return registeredCommands.execute(command);
	}
}
