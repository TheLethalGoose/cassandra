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

    public static long destroyAnswers(Session session, List<Answer> answersRef, int amount, boolean debug) {

        if(answersRef.isEmpty() || answersRef.size() < amount) {
            System.out.println("No answers found or amount to big. Please create answers first.");
            return -1;
        }

        DELETE DELETE = new DELETE(session, false);
        timer.start();

        for (int i = 0; i < amount; i++) {
            int indexToRemove = random.nextInt(answersRef.size());
            Answer victim = answersRef.remove(indexToRemove);
            DELETE.removeAnswer(victim);
        }

        long timeToDestroy = timer.getElapsedTime();

        if(debug) {
            System.out.println("Removed " + amount + " answers in " + timeToDestroy + " ms");
        }

        return timeToDestroy;

    }

}
