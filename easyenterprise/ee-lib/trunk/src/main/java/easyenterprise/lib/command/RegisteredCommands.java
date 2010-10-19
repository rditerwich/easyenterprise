package easyenterprise.lib.command;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class RegisteredCommands implements CommandService {

	@SuppressWarnings("unchecked")
	Map map = new HashMap();
	boolean registered;

	@SuppressWarnings("unchecked")
	public static RegisteredCommands create(final RegisteredCommands... registeredCommands) {
		return new RegisteredCommands() {
			protected void register() throws CommandException {
				for (RegisteredCommands registeredCommand : registeredCommands) {
					registeredCommand.doRegister();
					map.putAll(registeredCommand.map);
				}
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends CommandResult, C extends Command<T>> void register(CommandImpl<T, C> impl) throws CommandException {
		for (Method method : impl.getClass().getMethods()) {
			if (method.getName().equals("execute") && method.getParameterTypes().length == 1) {
				Class<?> commandClass = method.getParameterTypes()[0];
				if (Command.class.isAssignableFrom(commandClass)) {
					map.put(commandClass, impl);
				}
				return;
			}
		}
		throw new CommandException("No suitable command class found for registered command: " + impl);
	}

	@SuppressWarnings("unchecked")
	public <T extends CommandResult, C extends Command<T>> CommandImpl<T, C> getCommandImpl(C command) throws CommandException {
		doRegister();
		return (CommandImpl<T, C>) map.get(command.getClass());
	}
	
	public <T extends CommandResult, C extends Command<T>> T execute(C command) throws CommandException {
		CommandImpl<T, C> impl = getCommandImpl(command);
		if (impl == null) {
			throw new CommandNotImplementedException(command);
		}
		return AbstractCommandImpl.fullExecute(command, impl);
	}

	private final void doRegister() throws CommandException {
		if (!registered) {
			registered = true;
			register();
		}
	}
	
	protected abstract void register() throws CommandException;
}
