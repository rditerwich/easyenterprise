package easyenterprise.lib.command.gwt;

import java.util.HashMap;
import java.util.Map;

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
    private static Map<Command<?>, CacheResult> cache = new HashMap<Command<?>, CacheResult>(); 

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

	@SuppressWarnings("unchecked")
	public static <T extends CommandResult, C extends Command<T>> 
	void executeCached(final C command, long maxAge, final AsyncCallback<T> callback) {
		checkValid(command);
		CacheResult cacheResult = cache.get(command);
		if (cacheResult != null && System.currentTimeMillis() - cacheResult.time < maxAge) {
			callback.onSuccess((T) cacheResult.result);
		} else {
			executeWithRetry(command, 3, new RetryingCallback<T>() {
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				public void onSuccess(T result) {
					cache.put(command, new CacheResult(result));
					callback.onSuccess(result);
				}
				public void failedAttempt(int attemptNr, Throwable caught) {
				}
			});
		}
	}
	
	public static <T extends CommandResult, C extends Command<T>> 
	void invalidateCache(final C command) {
		cache.remove(command);
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
	
	private static class CacheResult {
		final long time = System.currentTimeMillis();
		final CommandResult result;
		public CacheResult(CommandResult result) {
			this.result = result;
		}
	}
}
