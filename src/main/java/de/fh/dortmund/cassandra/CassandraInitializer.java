package de.fh.dortmund.cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import de.fh.dortmund.helper.Timer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CassandraInitializer {

	static String stackoverflowScriptPath = "src/main/java/resources/stackoverflow.cql";
	static Timer timer = new Timer();

	public static void init(Session session) {

		try {
			timer.start();
			BufferedReader reader = new BufferedReader(new FileReader(stackoverflowScriptPath));
			String line;
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			reader.close();

			// CQL-Abfragen aus dem Skript ausführen
			String[] queries = stringBuilder.toString().split(";");
			for (String query : queries) {
				if (!query.trim().isEmpty()) {
					session.execute(query.trim());
				}
			}
			System.out.println("Initialized stackoverflow database in " + timer);
		} catch (IOException e) {
			System.out.println("Failed to read DDL script: " + e.getMessage());
		}
	}
	public static void flushData(Session session){

		timer.start();
		ResultSet tables = session.execute("SELECT table_name FROM system_schema.tables WHERE keyspace_name='stackoverflow'");
		for (com.datastax.driver.core.Row table : tables) {
			String tableName = table.getString("table_name");
			session.execute("TRUNCATE " + tableName);
		}
		System.out.println("Flushed stackoverflow database in " + timer);

	}
}

