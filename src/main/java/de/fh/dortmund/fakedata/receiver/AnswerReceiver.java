package de.fh.dortmund.fakedata.receiver;

import com.datastax.driver.core.Session;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.Question;
import de.fh.dortmund.service.GET;

import java.util.List;
import java.util.Random;

import static de.fh.dortmund.helper.Statistics.calculateMedian;

public class AnswerReceiver {

    static Timer timer = new Timer();
    static Random random = new Random();

    public static long medianTimeToFetchAnswersByQuestion(Session session, List<Question> questions, int iterations){
        long[] times = new long[iterations];

        GET get = new GET(session, false);

        for(int i = 0; i < iterations; i++){
            Question question = questions.get(random.nextInt(questions.size()));
            timer.start();
            get.getAnswersByQuestion(question);
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }

}
