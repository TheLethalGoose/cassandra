package de.fh.dortmund.cassandra;

import com.datastax.driver.core.Session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CassandraInitializer {

	static CassandraConnector CassandraConnector = new CassandraConnector();

	public static void init(String node, int port, String keyspace) {

		String stackoverflowScriptPath = "src/main/java/resources/stackoverflow.cql";

		CassandraConnector.connect("127.0.0.1", 9042,"stackoverflow");
		Session session = CassandraConnector.getSession();
		try {
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
		} catch (IOException e) {
			System.out.println("Failed to read DDL script: " + e.getMessage());
		}
		session.close();
	}
}

