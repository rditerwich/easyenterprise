package easyenterprise.lib.command.gwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;

public class GwtCommandFacade {

    private static GwtCommandServiceAsync asyncCommandService;

    public static final GwtCommandServiceAsync getAsyncCommandService() {
        if (asyncCommandService == null) {
        	asyncCommandService = (GwtCommandServiceAsync) GWT.create( GwtCommandService.class );
            ServiceDefTarget target = (ServiceDefTarget) asyncCommandService;
            target.setServiceEntryPoint( GWT.getModuleBaseURL() + "command" );
        }
        return asyncCommandService;
    }

	public static <T extends CommandResult, C extends Command<T>> 
	void execute(C command, final AsyncCallback<T> callback) {
		getAsyncCommandService().execute(command, callback);
	}

	public static <T extends CommandResult, C extends Command<T>> 
	void executeCached(C command, final AsyncCallback<T> callback) {
		getAsyncCommandService().execute(command, callback);
	}
	

}
