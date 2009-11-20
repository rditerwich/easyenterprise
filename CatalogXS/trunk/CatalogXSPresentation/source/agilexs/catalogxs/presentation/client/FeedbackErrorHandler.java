package agilexs.catalogxs.presentation.client;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.InvocationException;

import eu.easyenterprise.gwt.framework.client.core.Application;
import eu.easyenterprise.gwt.framework.client.core.ApplicationEventManager;
import eu.easyenterprise.gwt.framework.client.event.ErrorEvent;
import eu.easyenterprise.gwt.framework.client.event.ErrorEventHandler;

public class FeedbackErrorHandler implements ErrorEventHandler {

	/**
	 * Static initializer. Only call this method once during initialization of
	 * the Application.
	 */
	public static void init() {
		ApplicationEventManager.instance().addErrorEventHandler(
				new FeedbackErrorHandler());
	}

	// FIXME implement dedicated error messages for global error handler.
	public void onError(ErrorEvent event) {
		String message;
		final Throwable t = event.getThrowable();

		if (t instanceof IncompatibleRemoteServiceException) {
			// this client is not compatible with the server; cleanup and
			// refresh the
			message = "Refresh app";
		} else if (t instanceof InvocationException) {
			// the call didn't complete cleanly
			message = "Oops something went wrong...";
		} else {
			message = event.getMessage();
		}
		// This will leave the feedbackpanel color in the error color even for
		// the next message, so probably create different static calls to show
		// error and info messages.
		Application.getFeedbackPanel().showHTML(message, "#FC4D4D");
	}
}
