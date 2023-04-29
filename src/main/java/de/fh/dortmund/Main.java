package de.fh.dortmund;

import com.datastax.driver.core.Session;
import de.fh.dortmund.cassandra.CassandraConnector;
import de.fh.dortmund.cassandra.CassandraInitializer;
import de.fh.dortmund.fakedata.post.QuestionGenerator;
import de.fh.dortmund.fakedata.user.UserGenerator;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.model.User;
import de.fh.dortmund.service.POST;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String node = "127.0.0.1";
        int port = 9042;
        String keyspace = "stackoverflow";

        CassandraConnector connector = new CassandraConnector();
        connector.connect(node, port, keyspace);


        System.out.println("Welcome to stackoverflow");

        CassandraInitializer.init(connector.getSession());
        CassandraInitializer.flushData(connector.getSession());

        List<User> userList = UserGenerator.generateUsers(connector.getSession(),100);
        QuestionGenerator.generateQuestions(connector.getSession(), 100, userList);

        connector.close();
        System.out.println("Done");
    }
}