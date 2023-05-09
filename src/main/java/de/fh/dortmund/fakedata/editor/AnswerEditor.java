package de.fh.dortmund.fakedata.editor;

import com.datastax.driver.core.Session;
import com.github.javafaker.Faker;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.Answer;
import de.fh.dortmund.service.PUT;

import java.util.List;
import java.util.Random;

import static de.fh.dortmund.helper.Statistics.calculateMedian;

public class AnswerEditor {
    static Timer timer = new Timer();
    static Random random = new Random();

    static Faker faker = new Faker();

    public static long medianTimeToAcceptAnswer(Session session, List<Answer> answers, int iterations){
        long[] times = new long[iterations];

        PUT put = new PUT(session, false);

        for(int i = 0; i < iterations; i++){
            Answer answer = answers.get(random.nextInt(answers.size()));
            if(answer.isAccepted()) {
                continue;
            }
            timer.start();
            put.markAnswerAsAccepted(answer);
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }
}
