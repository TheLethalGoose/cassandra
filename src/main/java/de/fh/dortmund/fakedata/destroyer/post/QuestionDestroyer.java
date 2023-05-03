package de.fh.dortmund.fakedata.destroyer.post;

import com.datastax.driver.core.Session;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.model.Answer;
import de.fh.dortmund.model.Question;
import de.fh.dortmund.service.DELETE;

import java.util.List;
import java.util.Random;

public class QuestionDestroyer {

    static Timer timer = new Timer();
    static Random random = new Random();

    public static void destroyQuestions(Session session, List<Question> questions, List<Answer> answers, int amount) {

        if(questions.isEmpty() || questions.size() < amount) {
            System.out.println("No questions found or amount to big. Please create questions first.");
            return;
        }

        DELETE DELETE = new DELETE(session, false);
        timer.start();

        for (int i = 0; i < amount; i++) {
            int indexToRemove = random.nextInt(answers.size());
            Question victim = questions.remove(indexToRemove);
            DELETE.removeQuestion(victim);
        }

        System.out.println("Removed " + amount + " questions in " + timer);

    }

}

