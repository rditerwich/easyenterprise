package easyenterprise.server.party;

import java.util.HashMap;
import java.util.Map;

import easyenterprise.server.EEServer;

public class TestBase {

	private EEServer server;
	
	protected EEServer getServer() {
		if (server == null) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
			properties.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:unit-testing-jpa");
			properties.put("javax.persistence.jdbc.user", "sa");
			properties.put("javax.persistence.jdbc.password", "");
			
			server = new EEServer(properties);
		}
		return server;
	}
}
