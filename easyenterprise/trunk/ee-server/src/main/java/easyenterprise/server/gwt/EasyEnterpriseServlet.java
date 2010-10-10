package easyenterprise.server.gwt;

import java.io.Serializable;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.RegisteredCommands;
import easyenterprise.lib.command.gwt.GwtCommandResult;
import easyenterprise.lib.command.gwt.GwtCommandService;
import easyenterprise.server.impl.account.RegisteredAccountCommands;

public class EasyEnterpriseServlet extends RemoteServiceServlet implements GwtCommandService {

	private static final long serialVersionUID = 1L;

	private final RegisteredCommands registeredCommands = RegisteredCommands.create(new RegisteredAccountCommands());

	@Override
	public <T extends Serializable> GwtCommandResult<T> execute(Command<T> command) throws CommandException {
		GwtCommandResult<T> result = new GwtCommandResult<T>();
		result.value = registeredCommands.execute(command);
		return result;
	}
}
