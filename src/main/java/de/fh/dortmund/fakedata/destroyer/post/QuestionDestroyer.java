package de.fh.dortmund.fakedata.destroyer.post;

import com.datastax.driver.core.Session;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.Answer;
import de.fh.dortmund.models.Question;
import de.fh.dortmund.service.DELETE;

import java.util.List;
import java.util.Random;

public class QuestionDestroyer {

    static Timer timer = new Timer();
    static Random random = new Random();

    public static long destroyQuestions(Session session, List<Question> questionsRef, List<Answer> answersRef, int amount, boolean debug) {

        if(questionsRef.isEmpty() || questionsRef.size() < amount) {
            System.out.println("No questions found or amount to big. Please create questions first.");
            return -1;
        }

        DELETE DELETE = new DELETE(session, false);
        timer.start();

        for (int i = 0; i < amount; i++) {
            int indexToRemove = random.nextInt(questionsRef.size());
            Question victim = questionsRef.remove(indexToRemove);
            DELETE.removeQuestion(victim);

            for (int j = 0; j < answersRef.size(); j++) {

                if(answersRef.get(j).getParentPostId().equals(victim.getId())) {
                    Answer answer = answersRef.remove(j);
                    DELETE.removeAnswer(answer);
                }
            }

        }

        long timeToDestroy = timer.getElapsedTime();

        if(debug) {
            System.out.println("Removed " + amount + " questions in " + timeToDestroy + " ms");
        }

        return timeToDestroy;

    }

}

