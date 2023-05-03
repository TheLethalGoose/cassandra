package de.fh.dortmund;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.gson.JsonArray;
import de.fh.dortmund.cassandra.CassandraConnector;
import de.fh.dortmund.cassandra.CassandraInitializer;
import de.fh.dortmund.fakedata.destroyer.post.AnswerDestroyer;
import de.fh.dortmund.fakedata.destroyer.post.QuestionDestroyer;
import de.fh.dortmund.fakedata.destroyer.user.UserDestroyer;
import de.fh.dortmund.fakedata.generator.post.AnswerGenerator;
import de.fh.dortmund.fakedata.generator.post.QuestionGenerator;
import de.fh.dortmund.fakedata.generator.user.UserGenerator;
import de.fh.dortmund.json.JsonConverter;
import de.fh.dortmund.model.Answer;
import de.fh.dortmund.model.Question;
import de.fh.dortmund.model.Tag;
import de.fh.dortmund.model.User;
import de.fh.dortmund.service.GET;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        Set<Tag> tags = new HashSet<>();

        List<User> userList = UserGenerator.generateUsers(connector.getSession(),60);
        List<Question> questionList = QuestionGenerator.generateQuestions(connector.getSession(), 60, userList, tags);
        List<Answer> answerList = AnswerGenerator.generateAnswers(connector.getSession(), 60, userList, questionList);

        UserDestroyer.destroyUsers(connector.getSession(), userList, 10);
        AnswerDestroyer.destroyAnswers(connector.getSession(), answerList, 10);
        QuestionDestroyer.destroyQuestions(connector.getSession(), questionList, answerList, 10);

        GET GET = new GET(connector.getSession(), false);

        String jsonString = JsonConverter.jsonArrayToString(GET.getQuestionsByTag(tags.iterator().next().getName()));

        File file = new File(System.getProperty("user.home") + "/Desktop/questions_by_tag.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        connector.close();
        System.out.println("Done");
    }
}