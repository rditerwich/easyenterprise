package easyenterprise.lib.command.gwt;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;

@RemoteServiceRelativePath("command")
public interface GwtCommandService extends RemoteService {
	<T extends Serializable> GwtCommandResult<T> execute(Command<T> command) throws CommandException;
}
