package easyenterprise.lib.command.gwt;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;

@RemoteServiceRelativePath("command")
public interface GwtCommandService extends RemoteService {
	<T extends CommandResult> CommandResult execute(Command<T> command) throws CommandException;
}
