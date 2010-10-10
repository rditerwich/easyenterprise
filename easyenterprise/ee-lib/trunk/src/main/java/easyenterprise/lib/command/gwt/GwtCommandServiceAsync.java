package easyenterprise.lib.command.gwt;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import easyenterprise.lib.command.Command;

public interface GwtCommandServiceAsync {

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see GwtCommandService.client.util.CommandService
     */
	<T extends Serializable> void execute(Command<T> command, AsyncCallback<GwtCommandResult<T>> callback);
}
