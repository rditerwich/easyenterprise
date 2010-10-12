package easyenterprise.lib.command.gwt;

import com.google.gwt.user.client.rpc.AsyncCallback;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;

public interface GwtCommandServiceAsync {

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see GwtCommandService.client.util.CommandService
     */
	<T extends CommandResult> void execute(Command<T> command, AsyncCallback<T> callback);
}
