package de.fh.dortmund.fakedata.generator.post;

import com.datastax.driver.core.Session;
import com.github.javafaker.Faker;
import de.fh.dortmund.helper.LocalDateTimeGenerator;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.Answer;
import de.fh.dortmund.models.Question;
import de.fh.dortmund.models.User;
import de.fh.dortmund.service.POST;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnswerGenerator {

    static Faker faker = new Faker();
    static Timer timer = new Timer();

    public static long generateAnswers(Session session, List<Answer> answersRef, List<User> usersRef, List<Question> questionsRef, int amount, boolean debug) {

        if (usersRef.isEmpty() || questionsRef.isEmpty()) {
            System.out.println("No users or questions found. Please create users and questions first.");
            return -1;
        }

        List<Answer> answers = new ArrayList<>();
        POST POST = new POST(session, false);

        timer.start();

        for (int i = 0; i < amount; i++) {

            int randomUserIndex = (int) (Math.random() * usersRef.size());
            int randomQuestionIndex = (int) (Math.random() * questionsRef.size());

            String answerText = faker.lorem().sentence();
            User user = usersRef.get(randomUserIndex);
            Question question = questionsRef.get(randomQuestionIndex);
            int votes = faker.number().numberBetween(-100, 1000);

            //boolean accepted = faker.bool().bool();
            boolean accepted = false;

            LocalDateTime createdAt = LocalDateTimeGenerator.generateRandomLocalDateTimeAfter(LocalDateTime.parse(question.getCreatedAt()));
            LocalDateTime modifiedAt = LocalDateTimeGenerator.generateRandomLocalDateTimeAfter(createdAt);

            Answer newAnswer = new Answer(user.getId(), question.getId(), answerText, accepted, createdAt.toString(), modifiedAt.toString());
            answersRef.add(newAnswer);

            POST.createAnswer(newAnswer , question, votes);

        }

        long timeToCreate = timer.getElapsedTime();

        if(debug) {
            System.out.println("Created " + amount + " answers in " + timeToCreate);
        }

        return timeToCreate;

    }

}
