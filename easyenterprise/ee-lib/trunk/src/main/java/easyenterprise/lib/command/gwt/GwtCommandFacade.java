package easyenterprise.lib.command.gwt;

import java.io.Serializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import easyenterprise.lib.command.Command;

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

	public static <T extends Serializable, C extends Command<T>> void execute(C command, final AsyncCallback<T> callback) {
		getAsyncCommandService().execute(command, new AsyncCallback<GwtCommandResult<T>>() {
			public void onSuccess(GwtCommandResult<T> result) {
				callback.onSuccess((T) result.value);
			}
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}


}
