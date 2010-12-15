package easyenterprise.lib.command.gwt;

import com.google.gwt.user.client.rpc.AsyncCallback;

import easyenterprise.lib.command.CommandResult;

public interface RetryingCallback<T extends CommandResult> extends AsyncCallback<T> {
	void failedAttempt(int attemptNr, Throwable caught);
}
