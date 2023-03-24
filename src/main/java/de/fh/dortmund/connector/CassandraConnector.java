package de.fh.dortmund.connector;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class CassandraConnector {

    private static Cluster cluster;
    private static Session session;

    public void connect(String node, int port) {
        cluster = Cluster.builder().addContactPoint(node).withPort(port).build();
        session = cluster.connect();
    }

    public Session getSession(){
        return session;
    }

    public void close() {
        session.close();
        cluster.close();
    }

}
