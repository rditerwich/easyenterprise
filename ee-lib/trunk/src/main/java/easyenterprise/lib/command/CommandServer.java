package easyenterprise.lib.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import easyenterprise.lib.cloner.CloneException;
import easyenterprise.lib.cloner.Cloner;

public class CommandServer extends CommandWrapper implements CommandExecutor {

	protected Map<Class<? extends Command<?>>, Class<? extends CommandImpl<?>>> map = new HashMap<Class<? extends Command<?>>, Class<? extends CommandImpl<?>>>();
	protected List<CommandWrapper> wrappers = new ArrayList<CommandWrapper>();

	public CommandServer(RegisteredCommands... commands) {
		super(null);
		for (RegisteredCommands registeredCommands : commands) {
			for (Class<? extends CommandImpl<?>> implClass : registeredCommands.classes) {
				register(implClass);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
  private void register(Class<? extends CommandImpl<?>> implClass) {
		// find all super command classes that are not impls 
		for (Class<? extends Command<?>> commandClass = implClass; commandClass != null; commandClass = (Class<? extends Command<?>>) commandClass.getSuperclass()) {
			if (Command.class.isAssignableFrom(commandClass) && !CommandImpl.class.isAssignableFrom(commandClass)) {
				map.put(commandClass, implClass);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends CommandResult, C extends Command<T>> CommandImpl<T> createCommandImpl(C command) throws CommandException {
		Class<? extends CommandImpl<?>> implClass = map.get(command.getClass());
		if (implClass == null) {
			throw new CommandNotImplementedException(command);
		}
		try {
			return (CommandImpl<T>) Cloner.copyShallow(command, (Class<C>) implClass);
    } catch (CloneException e) {
			throw new CommandException("Couldn't create command implementation " + implClass.getName() + ": " + e.getMessage(), e);
    }
	}

	@Override
  public <T extends CommandResult, C extends Command<T>> T execute(C command) throws CommandException {
		return super.execute(command);
  }
	
	@Override
	protected <T extends CommandResult, C extends Command<T>> CommandImpl<T> createImpl(C command) throws CommandException {
		return createCommandImpl(command);
	}
	
	@Override
	protected <T extends CommandResult, I extends easyenterprise.lib.command.CommandImpl<T>> T executeImpl(I impl) throws CommandException {
		try {
			return impl.execute();
		} catch (CommandException e) {
			e.printStackTrace();
			throw e;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	protected <T extends CommandResult, I extends easyenterprise.lib.command.CommandImpl<T>> T postProcess(I impl, T result, CommandException e) throws CommandException {
		if (e != null) {
			throw e;
		}
		return result;
	};
}