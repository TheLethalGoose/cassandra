package de.fh.dortmund.service;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.fh.dortmund.json.JsonConverter;
import de.fh.dortmund.model.Answer;
import de.fh.dortmund.model.Post;
import de.fh.dortmund.model.Question;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    public JsonArray increaseValueToQuestion(Question question, String columnName, int byValue){

        GET GET = new GET(session, debug);

        timer.start();

        int increasedValue = GET.getValuesFromQuestion(question, columnName).get(0).getAsJsonObject().get("answers").getAsInt() + byValue;
        JsonArray tags = GET.getTagsFromQuestion(UUID.fromString(question.getId()));

        String increaseValueInQuestion = "UPDATE stackoverflow.question SET " + columnName + " = ? WHERE idQuestion = ?";
        PreparedStatement preparedStatementIncreaseValueInQuestion = session.prepare(increaseValueInQuestion);
        BoundStatement boundStatementIncreaseValueInQuestion= preparedStatementIncreaseValueInQuestion.bind(increasedValue, UUID.fromString(question.getId()));
        ResultSet resultSet = session.execute(boundStatementIncreaseValueInQuestion);

        for(JsonElement tag : tags){
            String increaseValueInQuestionsByTagQuery = "UPDATE stackoverflow.questions_by_tag SET " + columnName + " = ? WHERE tagName = ? AND idQuestion = ? AND createdAt = ?";
            PreparedStatement preparedStatementIncreaseValueInQuestionsByTag = session.prepare(increaseValueInQuestionsByTagQuery);
            BoundStatement boundStatementIncreaseValueInQuestionsByTag = preparedStatementIncreaseValueInQuestionsByTag.bind(increasedValue, tag.getAsString(), UUID.fromString(question.getId()), Timestamp.valueOf(LocalDateTime.parse(question.getCreatedAt())));
            session.execute(boundStatementIncreaseValueInQuestionsByTag);
        }

        if(debug){
            System.out.println("Increased answers to question by " + byValue + " in " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);

    }

}
