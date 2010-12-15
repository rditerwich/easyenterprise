package easyenterprise.lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class DBScript {

	private List<String> statements = new ArrayList<String>();
	
	public DBScript(String statements) throws IOException {
		read(new StringReader(statements));
	}
	
	public DBScript(InputStream is) throws IOException {
		read(new InputStreamReader(is));
	}
	
	public DBScript(InputStream is, Charset encoding) throws IOException {
		read(new InputStreamReader(is, encoding));
	}

	public DBScript read(Reader reader) throws IOException {
		BufferedReader r = new BufferedReader(reader);
		statements.clear();
		boolean shouldAdd = true;
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			line = line.trim();
			if (line.isEmpty()) continue;
			if (line.startsWith("-")) continue;
			if (shouldAdd) statements.add("");
			shouldAdd = line.endsWith(";");
			line = shouldAdd ? line.substring(0, line.length() - 1) : line;
			
//			if (line.contains("VARCHAR") && !line.contains("(")) line = line.replace("VARCHAR", "VARCHAR(200)");
//			line = line.replace("BOOLEAN", "INTEGER");
//			line = line.replace("SERIAL", "INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)");
//			line = line.replace("SERIAL", "IDENTITY");
//			line = line.replace("PRIMARY KEY (id)", "dummy  INTEGER");
//			line = line.replace("CREATE TABLE catalog.", "CREATE TABLE ");
			
			String statement = (statements.get(statements.size() - 1) + "\n" + line).trim(); 
			statements.set(statements.size() - 1, statement);
		}
		return this;
	}
	
	public List<String> getStatements() {
		return statements;
	}
	
	public Result execute(EntityManager em) {
		Result result = new Result(); 
		for (String statement : statements) {
			em.getTransaction().begin();
			try {
				Query query = em.createNativeQuery(statement);
				query.executeUpdate();
			} catch (Throwable e) {
				result.put(new SQLException(e), statement);
			} finally {
				if (em.getTransaction().getRollbackOnly()) {
					em.getTransaction().rollback();
				} else {
					em.getTransaction().commit();
				}
			}
		}
		return result;
	}

	public Result execute(Connection connection) {
		Result result = new Result(); 
		for (String statement : statements) {
			try {
				connection.createStatement().execute(statement);
			} catch (SQLException e) {
				result.put(e, statement);
			}
		}
		return result;
	}
	
	public static class Result extends HashMap<SQLException, String> {

		private static final long serialVersionUID = 1L;
		
		public void assertSuccess() throws SQLException {
			if (!isEmpty()) {
				StringBuilder out = new StringBuilder("DBScript execution failure:");
				for (Map.Entry<SQLException, String> entry : entrySet()) {
					out.append("\n" + entry.getKey() + "\n" + entry.getValue());
				}
				throw new SQLException(out.toString());
			}
		}
	}
}
