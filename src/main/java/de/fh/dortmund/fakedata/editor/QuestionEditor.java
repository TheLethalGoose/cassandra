package de.fh.dortmund.fakedata.editor;

import com.datastax.driver.core.Session;
import com.github.javafaker.Faker;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.Question;
import de.fh.dortmund.models.enums.VoteType;
import de.fh.dortmund.service.PUT;

import java.util.List;
import java.util.Random;

import static de.fh.dortmund.helper.Statistics.calculateMedian;

public class QuestionEditor {

    static Timer timer = new Timer();
    static Random random = new Random();

    static Faker faker = new Faker();

    public static long medianTimeToEditQuestions(Session session, List<Question> questions, int iterations){
        long[] times = new long[iterations];

        PUT put = new PUT(session, false);

        for(int i = 0; i < iterations; i++){
            Question question = questions.get(random.nextInt(questions.size()));
            timer.start();
            put.editQuestion(question,"I got edited by a faker");
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }
    public static long medianTimeToVoteUPDATE(Session session, List<Question> questions, int iterations){
        long[] times = new long[iterations];

        PUT put = new PUT(session, false);

        for(int i = 0; i < iterations; i++){
            Question question = questions.get(random.nextInt(questions.size()));
            timer.start();
            put.voteByUpdate(question, VoteType.UPVOTE);
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }
    public static long medianTimeToVoteINSERT(Session session, List<Question> questions, int iterations){
        long[] times = new long[iterations];

        PUT put = new PUT(session, false);

        for(int i = 0; i < iterations; i++){
            Question question = questions.get(random.nextInt(questions.size()));
            timer.start();
            put.voteByInsert(question, VoteType.DOWNVOTE);
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }

}
