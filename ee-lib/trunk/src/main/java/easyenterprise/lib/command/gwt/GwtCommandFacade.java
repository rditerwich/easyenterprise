package easyenterprise.lib.command.gwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.StatusCodeException;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

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
		checkValid(command);
		getAsyncCommandService().execute(command, callback);
	}

	public static <T extends CommandResult, C extends Command<T>> 
	void executeCached(C command, final AsyncCallback<T> callback) {
		checkValid(command);
		getAsyncCommandService().execute(command, callback);
	}
	
	// TODO maybe we should have some sort of timeout, or more subtle retry mechanism....
	public static <T extends CommandResult, C extends Command<T>> 
	void executeWithRetry(final C command, final int nrAttempts, final RetryingCallback<T> callback) {
		checkValid(command);
		getAsyncCommandService().execute(command, new AsyncCallback<T>() {
			int attemptNr = 0;
			public void onSuccess(T result) {
				callback.onSuccess(result);
			}
			public void onFailure(Throwable caught) {
				if (caught instanceof InvocationException && !(caught instanceof StatusCodeException)) {
					callback.failedAttempt(++attemptNr, caught);
					if (attemptNr < nrAttempts) {
						getAsyncCommandService().execute(command, this);
					}
				} else {
					callback.onFailure(caught);
				}
			}
		});
	}
	
	private static <C extends Command<?>> void checkValid(C command) {
		try {
			command.checkValid();
		} catch (CommandValidationException e) {
			throw new RuntimeException(e);
		}
	}
}
