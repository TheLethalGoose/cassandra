package de.fh.dortmund.service;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.google.gson.JsonArray;
import de.fh.dortmund.json.JsonConverter;
import de.fh.dortmund.model.Question;
import de.fh.dortmund.model.Tag;
import de.fh.dortmund.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

public class GET extends REST {

    public GET(Session session, boolean debug) {
        super(session, debug);
    }

    public JsonArray getTagsFromQuestion(Question question){
        return getTagsFromQuestion(UUID.fromString(question.getIdPost()));
    }
    public JsonArray getTagsFromQuestion(UUID idQuestion){
        PreparedStatement getTagsFromQuestionStatement = session.prepare("SELECT tags FROM stackoverflow.questions WHERE idQuestion = ?");
        BoundStatement getTagsFromQuestionStatemenBoundStatement = getTagsFromQuestionStatement.bind(idQuestion);
        ResultSet resultSet = session.execute(getTagsFromQuestionStatemenBoundStatement);

        //Object with Array in Array
        return JsonConverter.resultSetToJsonArray(resultSet).get(0).getAsJsonObject().get("tags").getAsJsonArray();
    }

    public JsonArray getQuestionsByTag(Tag tag){
        //TODO
        return null;
    }
    public JsonArray getQuestionsByTag(String tagName){
        //TODO
        return null;
    }

    public JsonArray getQuestionsByUser(String userName){
        //TODO
        return null;
    }
    public JsonArray getQuestionsByUser(User user){
        //TODO
        return null;
    }

    public JsonArray getAnswersByQuestion(Question question){
        //TODO
        return null;
    }
    public JsonArray getAnswersByQuestion(UUID idQuestion){
        //TODO
        return null;
    }

    public JsonArray getQuestionByDate(String dateString){
        //TODO
        return null;
    }
    public JsonArray getQuestionByDate(LocalDateTime date){
        //TODO
        return null;
    }
    public JsonArray getUserByEmail(String email){
        //TODO
        return null;
    }
    public JsonArray findUser(User user){
        //TODO
        return null;
    }
    public JsonArray findUser(UUID uuid){
        //TODO
        return null;
    }

}
