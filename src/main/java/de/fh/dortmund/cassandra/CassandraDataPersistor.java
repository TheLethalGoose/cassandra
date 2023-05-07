package de.fh.dortmund.cassandra;

import com.datastax.driver.core.Session;
import de.fh.dortmund.models.*;
import de.fh.dortmund.models.cross.TagQuestion;
import de.fh.dortmund.service.POST;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CassandraDataPersistor {

    private Session session;

    public CassandraDataPersistor(Session session) {
        this.session = session;
    }

    public void persistUsers(List<User> users){
        POST post = new POST(session, false);
        for (User user : users) {
            post.createUser(user);
        }
    }
    public void persistQuestions(List<Question> questions, List<Vote> votes, List<Answer> answers, List<TagQuestion> tagQuestions, List<Tag> tags){

        POST post = new POST(session, false);

        for (Question question : questions) {

            HashSet<Tag> tagsToQuestion = new HashSet<>();
            int answerCount = countAnswers(answers, question.getId());
            int nettoVotes = countNettoVotes(votes, question.getId());

            for (TagQuestion tagQuestion : tagQuestions) {
                if(Objects.equals(tagQuestion.getTagId(), question.getId())){
                    tagsToQuestion.add(tags.stream().filter(tag -> Objects.equals(tag.getId(), tagQuestion.getTagId())).findFirst().get());
                }
            }

            post.createQuestion(question, tagsToQuestion, nettoVotes, question.getViews(), answerCount);

        }
    }
    public void persistAnswers(List<Answer> answers, List<Vote> votes){

        POST post = new POST(session, false);

        for (Answer answer : answers) {
            int nettoVotes = countNettoVotes(votes, answer.getId());
            post.createAnswer(answer, UUID.fromString(answer.getIdParent()), nettoVotes);
        }
    }
    private int countNettoVotes(List<Vote> votes, String postId){
        int nettoVotes = 0;
        for (Vote vote : votes) {
            if(Objects.equals(vote.getPostId(), postId)){
                if(vote.isUpvote()) {
                    nettoVotes++;
                }
                if(vote.isDownVote()){
                    nettoVotes--;
                }
            }
        }
        return nettoVotes;
    }
    private int countAnswers(List<Answer> answers, String questionId){
        int answerCount = 0;
        for (Answer answer : answers) {
            if(Objects.equals(answer.getIdParent(), questionId)){
                answerCount++;
            }
        }
        return answerCount;
    }

}
