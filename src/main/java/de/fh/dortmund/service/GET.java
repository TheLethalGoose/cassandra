package de.fh.dortmund.service;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.fh.dortmund.json.JsonConverter;
import de.fh.dortmund.model.Question;

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



}
