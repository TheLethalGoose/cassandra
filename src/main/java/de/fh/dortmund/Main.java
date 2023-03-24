package de.fh.dortmund;

import com.datastax.driver.core.Session;
import de.fh.dortmund.connector.CassandraConnector;
import de.fh.dortmund.json.JsonConverter;

public class Main {
    public static void main(String[] args) {

        CassandraConnector connector = new CassandraConnector();
        connector.connect("127.0.0.1", 9042);

        Session session = connector.getSession();

        JsonConverter converter = new JsonConverter(session);


    }
}