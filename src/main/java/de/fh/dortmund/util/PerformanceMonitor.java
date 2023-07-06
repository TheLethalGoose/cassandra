package de.fh.dortmund.util;

import com.datastax.driver.core.Session;
import de.fh.dortmund.cassandra.CassandraInitializer;
import de.fh.dortmund.fakedata.destroyer.post.AnswerDestroyer;
import de.fh.dortmund.fakedata.destroyer.post.QuestionDestroyer;
import de.fh.dortmund.fakedata.destroyer.user.UserDestroyer;
import de.fh.dortmund.fakedata.editor.AnswerEditor;
import de.fh.dortmund.fakedata.editor.QuestionEditor;
import de.fh.dortmund.fakedata.generator.post.AnswerGenerator;
import de.fh.dortmund.fakedata.generator.post.QuestionGenerator;
import de.fh.dortmund.fakedata.generator.user.UserGenerator;
import de.fh.dortmund.fakedata.receiver.AnswerReceiver;
import de.fh.dortmund.fakedata.receiver.QuestionReceiver;
import de.fh.dortmund.fakedata.receiver.UserReceiver;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.Answer;
import de.fh.dortmund.models.Question;
import de.fh.dortmund.models.Tag;
import de.fh.dortmund.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.fh.dortmund.helper.Statistics.*;
import static de.fh.dortmund.helper.Timer.convertMilliSeconds;

public class PerformanceMonitor {

    Session session;

    int fetchIterations;
    int ddlIterations;

    List<User> users = new ArrayList<>();
    List<Question> questions = new ArrayList<>();
    List<Answer> answers = new ArrayList<>();
    Set<Tag> tags = new HashSet<>();

    long[] generatedUsersInTimes;
    long[] generatedQuestionsInTimes;
    long[] generatedAnswersInTimes;
    long[] destroyedUsersInTimes;
    long[] destroyedQuestionsInTimes;
    long[] destroyedAnswersInTimes;
    long[] timesToGenerate;
    long[] timesToDestroy;

    public PerformanceMonitor(Session session, int ddlIterations, int fetchIterations) {
        this.session = session;
        this.ddlIterations = ddlIterations;
        this.fetchIterations = fetchIterations;

        generatedUsersInTimes = new long[ddlIterations];
        generatedQuestionsInTimes = new long[ddlIterations];
        generatedAnswersInTimes = new long[ddlIterations];
        destroyedUsersInTimes = new long[ddlIterations];
        destroyedQuestionsInTimes = new long[ddlIterations];
        destroyedAnswersInTimes = new long[ddlIterations];
        timesToGenerate = new long[ddlIterations];
        timesToDestroy = new long[ddlIterations];
    }
    public void runPerformanceTest(){

        System.out.println("Starting performance test on cassandra stackoverflow database");
        CassandraInitializer.flushData(session, "stackoverflow");
        System.out.println("------------------------------------------------------------------");

        //Create and destroy data test
        createAndDestroyDataTest(100, 150, 200, 5, 50, 100, 50);

        //Fetch and edit data test
        generateTestData(100, 100, 200, 5);

        fetchDataTest();
        editDataTest();

    }
    private void createAndDestroyDataTest(int usersToGenerate, int questionsToGenerate, int answersToGenerate, int tagsToGenerate, int questionsToDestroy, int answersToDestroy, int usersToDestroy){

        Timer timer = new Timer();

        for(int i = 0; i < ddlIterations; i++){

            System.out.println("Create/Remove Iteration " + (i+1) + " of " + ddlIterations);

            timer.start();

            long generatedUsersIn = UserGenerator.generateUsers(session,users, usersToGenerate, false);
            long generatedQuestionsIn = QuestionGenerator.generateQuestions(session, questions, users, tags, questionsToGenerate, tagsToGenerate, false);
            long generatedAnswersIn = AnswerGenerator.generateAnswers(session, answers, users, questions, answersToGenerate, false);

            long destroyedUsersIn = UserDestroyer.destroyUsers(session, users, usersToDestroy, false);
            long destroyedQuestionsIn = QuestionDestroyer.destroyQuestions(session, questions, answers, questionsToDestroy,false);
            long destroyedAnswersIn = AnswerDestroyer.destroyAnswers(session, answers, answersToDestroy, false);

            long generatedIn = timer.getElapsedTime();

            generatedUsersInTimes[i] = generatedUsersIn;
            generatedQuestionsInTimes[i] = generatedQuestionsIn;
            generatedAnswersInTimes[i] = generatedAnswersIn;
            timesToGenerate[i] = generatedIn;

            destroyedUsersInTimes[i] = destroyedUsersIn;
            destroyedQuestionsInTimes[i] = destroyedQuestionsIn;
            destroyedAnswersInTimes[i] = destroyedAnswersIn;
            timesToDestroy[i] = timer.getElapsedTime();

            if(ddlIterations == 1){
                System.out.println("Finsished generating test data in " + convertMilliSeconds(generatedIn));
                System.out.println("Generated " + usersToGenerate + " users in " + convertMilliSeconds(generatedUsersIn));
                System.out.println("Generated " + questionsToGenerate + " questions in " + convertMilliSeconds(generatedQuestionsIn));
                System.out.println("Generated " + answersToGenerate + " answers in " + convertMilliSeconds(generatedAnswersIn));
                System.out.println("-----------------------------------------");
                System.out.println("Finsished destroying test data in " + convertMilliSeconds(timer.getElapsedTime()));
                System.out.println("Destroyed " + usersToDestroy + " users in " + convertMilliSeconds(destroyedUsersIn));
                System.out.println("Destroyed " + questionsToDestroy + " questions in " + convertMilliSeconds(destroyedQuestionsIn));
                System.out.println("Destroyed " + answersToDestroy + " answers in " + convertMilliSeconds(destroyedAnswersIn));
            }

        }

        if(ddlIterations > 1){
            System.out.println("Average time of generating users: " + convertMilliSeconds(calculateAverage(generatedUsersInTimes)));
            System.out.println("Median time of generating users: " + convertMilliSeconds(calculateMedian(generatedUsersInTimes)));
            System.out.println("Min time of generating users: " + convertMilliSeconds(min(generatedUsersInTimes)));
            System.out.println("Max time of generating users: " + convertMilliSeconds(max(generatedUsersInTimes)));
            System.out.println("-----------------------------------------");

            System.out.println("Average time of generating questions: " + convertMilliSeconds(calculateAverage(generatedQuestionsInTimes)));
            System.out.println("Median time of generating questions: " + convertMilliSeconds(calculateMedian(generatedQuestionsInTimes)));
            System.out.println("Min time of generating questions: " + convertMilliSeconds(min(generatedQuestionsInTimes)));
            System.out.println("Max time of generating questions: " + convertMilliSeconds(max(generatedQuestionsInTimes)));
            System.out.println("-----------------------------------------");

            System.out.println("Average time of generating answers: " + convertMilliSeconds(calculateAverage(generatedAnswersInTimes)));
            System.out.println("Median time of generating answers: " + convertMilliSeconds(calculateMedian(generatedAnswersInTimes)));
            System.out.println("Min time of generating answers: " + convertMilliSeconds(min(generatedAnswersInTimes)));
            System.out.println("Max time of generating answers: " + convertMilliSeconds(max(generatedAnswersInTimes)));
            System.out.println("-----------------------------------------");

            System.out.println("-----------------------------------------");
            System.out.println("Average time of generating test data: " + convertMilliSeconds(calculateAverage(timesToGenerate)));
            System.out.println("Median time of generating test data: " + convertMilliSeconds(calculateMedian(timesToGenerate)));
            System.out.println("Min time of generating test data: " + convertMilliSeconds(min(timesToGenerate)));
            System.out.println("Max time of destroying test data: " + convertMilliSeconds(max(timesToGenerate)));
            System.out.println("-----------------------------------------");

            System.out.println("Average time of destroying test data: " + convertMilliSeconds(calculateAverage(timesToDestroy)));
            System.out.println("Median time of destroying test data: " + convertMilliSeconds(calculateMedian(timesToDestroy)));
            System.out.println("Min time of destroying test data: " + convertMilliSeconds(min(timesToDestroy)));
            System.out.println("Max time of destroying test data: " + convertMilliSeconds(max(timesToDestroy)));

        }

        System.out.println("------------------------------------------------------------------");

    }
    private void fetchDataTest(){

        System.out.println("Starting fetch data test");

        System.out.println("Median time to fetch users by their UUID of " + fetchIterations + " iterations: " + convertMilliSeconds(UserReceiver.medianTimeToFetchUsersByUUID(session, users, fetchIterations)));
        System.out.println("Median time to fetch users by their email of " + fetchIterations + " iterations: " + convertMilliSeconds(UserReceiver.medianTimeToFetchUsersByEmail(session, users, fetchIterations)));

        System.out.println("Median time to fetch questions by their UUID of " + fetchIterations + " iterations: " + convertMilliSeconds(QuestionReceiver.medianTimeToFetchQuestionsByUUID(session, questions, fetchIterations)));
        System.out.println("Median time to fetch questions by their creator of " + fetchIterations + " iterations: " + convertMilliSeconds(QuestionReceiver.medianTimeToFetchQuestionsByUser(session, questions, users, fetchIterations)));
        System.out.println("Median time to fetch questions by their tag of " + fetchIterations + " iterations: " + convertMilliSeconds(QuestionReceiver.medianTimeToFetchQuestionsByTag(session, tags, fetchIterations)));
        System.out.println("Median time to fetch latest questions of " + fetchIterations + " iterations: " + convertMilliSeconds(QuestionReceiver.medianTimeToFetchLatestQuestions(session, fetchIterations)));
        System.out.println("Median time to fetch votes by their question of " + fetchIterations + " iterations: " + convertMilliSeconds(QuestionReceiver.medianTimeToFetchVotes(session, questions, fetchIterations)));

        System.out.println("Median time to fetch answers by their question of " + fetchIterations + " iterations: " + convertMilliSeconds(AnswerReceiver.medianTimeToFetchAnswersByQuestion(session, questions, fetchIterations)));

        System.out.println("------------------------------------------------------------------");
    }

    private void editDataTest(){

        System.out.println("Starting edit data test");

        System.out.println("Median time to edit content of question of " + fetchIterations + " iterations: " + convertMilliSeconds(QuestionEditor.medianTimeToEditQuestions(session, questions, fetchIterations)));
        System.out.println("Median time to mark answer as accepted of " + fetchIterations + " iterations: " + convertMilliSeconds(AnswerEditor.medianTimeToAcceptAnswer(session, answers, fetchIterations)));
        System.out.println("Median time to upvote on a question by UPDATE of " + fetchIterations + " iterations: " + convertMilliSeconds(QuestionEditor.medianTimeToVoteUPDATE(session, questions, fetchIterations)));
        System.out.println("Median time to downvote on a question by INSERT of " + fetchIterations + " iterations: " + convertMilliSeconds(QuestionEditor.medianTimeToVoteINSERT(session, questions, fetchIterations)));

        System.out.println("------------------------------------------------------------------");
    }

    private void generateTestData(int usersToGenerate, int questionsToGenerate, int answersToGenerate, int maxTagsToGeneratePerQuestion){
        System.out.println("Generating test data");

        UserGenerator.generateUsers(session,users, usersToGenerate, false);
        QuestionGenerator.generateQuestions(session, questions, users, tags, questionsToGenerate, maxTagsToGeneratePerQuestion, false);
        AnswerGenerator.generateAnswers(session, answers, users, questions, answersToGenerate, false);
    }
}
