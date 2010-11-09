package easyenterprise.lib.command;

import java.util.ArrayList;
import java.util.List;

public abstract class RegisteredCommands {

	List<Class<? extends CommandImpl<?>>> classes = new ArrayList<Class<? extends CommandImpl<?>>>();

	public static RegisteredCommands create(final RegisteredCommands... registeredCommands) {
		return new RegisteredCommands() {{
				for (RegisteredCommands registeredCommand : registeredCommands) {
					classes.addAll(registeredCommand.classes);
			}
		}};
	}
	
	protected <T extends CommandResult, I extends Command<T> & CommandImpl<T>> void register(Class<I> impl) {
		classes.add(impl);
	}
}
