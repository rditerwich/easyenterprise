package citykids.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.gwt.GwtCommandService;
import easyenterprise.server.EEServer;

public class CityKidsServlet extends RemoteServiceServlet implements GwtCommandService {

	private static final long serialVersionUID = 1L;

	private EEServer server;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			config.getClass().getClassLoader().loadClass("org.eclipse.persistence.Version");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		server = new EEServer(config);
	}
	
	@Override
	public <T extends CommandResult> T execute(Command<T> command) throws CommandException {
		return server.execute(command);
	}
	
}
