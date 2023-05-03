package de.fh.dortmund.service;

import com.datastax.driver.core.Session;
import com.google.gson.JsonArray;
import de.fh.dortmund.model.Answer;
import de.fh.dortmund.model.Post;
import de.fh.dortmund.model.Question;

import java.util.Set;
import java.util.UUID;

public class PUT extends REST{
    public PUT(Session session, boolean debug) {
        super(session, debug);
    }

    public JsonArray editQuestion(Question question, String alteredContent){
        //TODO
        return null;
    }
    public JsonArray editQuestion(UUID idQuestion, String alteredContent){
        //TODO
        return null;
    }

    public JsonArray markAnswerAsAccepted(Answer answer){
        //TODO
        return null;
    }
    public JsonArray markAnswerAsAccepted(UUID idAnswer){
        //TODO
        return null;
    }

    public JsonArray votePost(Post post , boolean upvote){
        //TODO
        return null;
    }

    public JsonArray addRelatedQuestions(Question question , Set<Question> relatedQuestionsSet){
        //TODO
        return null;
    }

    public JsonArray addLinkedQuestions(Question question , Set<Question> linkedQuestionsSet){
        //TODO
        return null;
    }

}
