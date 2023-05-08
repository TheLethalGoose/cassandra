package de.fh.dortmund.fakedata.receiver;

import com.datastax.driver.core.Session;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.Question;
import de.fh.dortmund.models.Tag;
import de.fh.dortmund.models.User;
import de.fh.dortmund.service.GET;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static de.fh.dortmund.helper.Statistics.calculateMedian;

public class QuestionReceiver {

    static Timer timer = new Timer();
    static Random random = new Random();

    public static long medianTimeToFetchQuestionsByUUID(Session session, List<Question> questions, int iterations){
        long[] times = new long[iterations];

        GET get = new GET(session, false);

        for(int i = 0; i < iterations; i++){
            Question question = questions.get(random.nextInt(questions.size()));
            timer.start();
            get.getQuestion(question);
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }
    public static long medianTimeToFetchQuestionsByTag(Session session, Set<Tag> tags, int iterations){
        long[] times = new long[iterations];

        GET get = new GET(session, false);

        for(int i = 0; i < iterations; i++){
            Tag[] tagsArray = tags.toArray(new Tag[0]);
            Tag tag = tagsArray[random.nextInt(tagsArray.length)];
            timer.start();
            get.getQuestionsByTag(tag);
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }
    public static long medianTimeToFetchQuestionsByUser(Session session, List<Question> questions, List<User> users, int iterations){
        long[] times = new long[iterations];

        GET get = new GET(session, false);

        for(int i = 0; i < iterations; i++){
            User user = users.get(random.nextInt(users.size()));
            timer.start();
            get.getQuestionsByUser(user);
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }

    public static long medianTimeToFetchLatestQuestions(Session session, int iterations) {
        long[] times = new long[iterations];

        GET get = new GET(session, false);

        for (int i = 0; i < iterations; i++) {
            timer.start();
            get.getLatestQuestions();
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }

    public static long medianTimeToFetchVotes(Session session, List<Question> questions, int iterations) {
        long[] times = new long[iterations];

        GET get = new GET(session, false);

        for (int i = 0; i < iterations; i++) {

            Question question = questions.get(random.nextInt(questions.size()));
            timer.start();
            get.getValuesFromPost(question, Collections.singleton("votes"));
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }

}
