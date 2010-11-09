package easyenterprise.lib.command;

public abstract class CommandWrapper implements CommandExecutor {

	private final CommandWrapper delegate;

	public CommandWrapper(CommandWrapper delegate) {
		this.delegate = delegate;
	}
	
	protected <T extends CommandResult, C extends Command<T>> CommandImpl<T> createImpl(C command) throws CommandException {
		return delegate.createImpl(command);
	}
	
	protected <T extends CommandResult, I extends CommandImpl<T>> T executeImpl(I impl) throws CommandException {
		return delegate.executeImpl(impl);
	}
	
	protected <T extends CommandResult, I extends CommandImpl<T>> T postProcess(I impl, T result, CommandException e) throws CommandException {
		return delegate.postProcess(impl, result, e);
	}
	
	public <T extends CommandResult, C extends Command<T>> T execute(C command) throws CommandException {
		CommandImpl<T> impl = createImpl(command);
		T rawResult;
    try {
	    rawResult = executeImpl(impl);
	    return postProcess(impl, rawResult, null);
    } catch (CommandException e) {
	    return postProcess(impl, null, e);
    }
	}
}
