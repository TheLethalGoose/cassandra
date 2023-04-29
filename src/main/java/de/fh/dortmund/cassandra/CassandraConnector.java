package de.fh.dortmund.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class CassandraConnector {

    private static Cluster cluster;
    private static Session session;

    public void connect(String node, int port, String keyspace) {
        cluster = Cluster.builder().addContactPoint(node).withPort(port).build();
        session = cluster.connect();

        String createKeyspace = "CREATE KEYSPACE IF NOT EXISTS stackoverflow WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}";
        session.execute(createKeyspace);
        session.execute("USE " + keyspace);
    }

    public Session getSession(){
        return session;
    }

    public void close() {
        session.close();
        cluster.close();
    }

}
