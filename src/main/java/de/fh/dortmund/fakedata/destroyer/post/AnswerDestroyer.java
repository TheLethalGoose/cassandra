package de.fh.dortmund.fakedata.destroyer.post;

import com.datastax.driver.core.Session;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.Answer;
import de.fh.dortmund.service.DELETE;

import java.util.List;
import java.util.Random;

public class AnswerDestroyer {

    static Timer timer = new Timer();
    static Random random = new Random();

    public static void destroyAnswers(Session session, List<Answer> answers, int amount) {

        if(answers.isEmpty() || answers.size() < amount) {
            System.out.println("No answers found or amount to big. Please create answers first.");
            return;
        }

        DELETE DELETE = new DELETE(session, false);
        timer.start();

        for (int i = 0; i < amount; i++) {
            int indexToRemove = random.nextInt(answers.size());
            Answer victim = answers.remove(indexToRemove);
            DELETE.removeAnswer(victim);
        }

        System.out.println("Removed " + amount + " answers in " + timer);

    }

}
