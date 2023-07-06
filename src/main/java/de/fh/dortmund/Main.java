package de.fh.dortmund;

import de.fh.dortmund.cassandra.CassandraConnector;
import de.fh.dortmund.cassandra.CassandraInitializer;
import de.fh.dortmund.util.PerformanceMonitor;

public class Main {

    public static void main(String[] args) {

        String node = "127.0.0.1";
        int port = 9042;
        String keyspace = "stackoverflow";

        CassandraConnector connector = new CassandraConnector();
        connector.connect(node, port, keyspace);

        System.out.println("Welcome to stackoverflow");
        CassandraInitializer.init(connector.getSession(), false, false);

        //Tombstone threshold zur Vorstellung in der Präsentation
        //CassandraInitializer.setTombstoneThreshold(connector.getSession(),keyspace, 60);

        PerformanceMonitor monitor = new PerformanceMonitor(connector.getSession(),1,10);
        monitor.runPerformanceTest();

        connector.close();
        System.out.println("Goodbye");

    }
}