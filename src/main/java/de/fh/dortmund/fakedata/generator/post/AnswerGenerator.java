package de.fh.dortmund.fakedata.generator.post;

import com.datastax.driver.core.Session;
import com.github.javafaker.Faker;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.model.Answer;
import de.fh.dortmund.model.Question;
import de.fh.dortmund.model.User;
import de.fh.dortmund.service.POST;

import java.util.ArrayList;
import java.util.List;

public class AnswerGenerator {

    static Faker faker = new Faker();
    static Timer timer = new Timer();

    public static List<Answer> generateAnswers(Session session, int amount, List<User> users, List<Question> questions) {

        if (users.isEmpty() || questions.isEmpty()) {
            System.out.println("No users or questions found. Please create users and questions first.");
            return null;
        }

        List<Answer> answers = new ArrayList<>();
        POST POST = new POST(session, false);

        timer.start();

        for (int i = 0; i < amount; i++) {

            int randomUserIndex = (int) (Math.random() * users.size());
            int randomQuestionIndex = (int) (Math.random() * questions.size());

            String answerText = faker.lorem().sentence();
            User user = users.get(randomUserIndex);
            Question question = questions.get(randomQuestionIndex);
            int votes = faker.number().numberBetween(-100, 1000);

            Answer newAnswer = new Answer(user.getId(), question.getId(), answerText);
            answers.add(newAnswer);

            POST.createAnswer(newAnswer , question, votes);

        }
        System.out.println("Created " + amount + " answers in " + timer);

        return answers;

    }

}
