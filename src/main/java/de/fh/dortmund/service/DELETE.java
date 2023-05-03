package de.fh.dortmund.service;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.fh.dortmund.json.JsonConverter;
import de.fh.dortmund.model.Answer;
import de.fh.dortmund.model.Question;
import de.fh.dortmund.model.User;

import java.sql.Timestamp;
import java.util.UUID;

public class DELETE extends REST{

    public DELETE(Session session, boolean debug) {
        super(session, debug);
    }

    public JsonArray removeUser(User user){
        ResultSet resultSet = removeUser(UUID.fromString(user.getIdUser()), user.getEmail());
        return JsonConverter.resultSetToJsonArray(resultSet);
    }
    public JsonArray removeAnswer(Answer answer){
        ResultSet resultSet = removeAnswer(UUID.fromString(answer.getParentPostId()), UUID.fromString(answer.getIdPost()));
        return JsonConverter.resultSetToJsonArray(resultSet);
    }
    public JsonArray removeQuestion(Question question){

        //Tags zur Frage holen
        GET GET = new GET(session, debug);
        JsonArray tags = GET.getTagsFromQuestion(UUID.fromString(question.getIdPost()));

        ResultSet resultSet = removeQuestion(UUID.fromString(question.getIdPost()), tags, Timestamp.valueOf(question.getCreatedAt()));
        return JsonConverter.resultSetToJsonArray(resultSet);
    }
    private ResultSet removeAnswer(UUID idQuestion, UUID idAnswer) {
        timer.start();

        // Löschen der Antwort in der Tabelle "answers_by_question"
        PreparedStatement removeAnswerStatement = session.prepare("DELETE FROM stackoverflow.answers_by_question WHERE idQuestion = ? AND idAnswer = ?");
        BoundStatement removeAnswerBoundStatement = removeAnswerStatement.bind(idQuestion, idAnswer);
        session.execute(removeAnswerBoundStatement);

        ResultSet resultSet = session.execute(removeAnswerBoundStatement);

        timer.stop();

        if(debug) {
            System.out.println("Deleted answer " + idAnswer.toString() + " to question " + idQuestion.toString() + " in " + timer);
        }

        return resultSet;
    }
    private ResultSet removeUser(UUID uuid, String email){
        timer.start();

        // Löschen des Nutzers in der Tabelle "user"
        PreparedStatement removeUserStatement = session.prepare("DELETE FROM stackoverflow.user WHERE idUser = ?");
        BoundStatement removeUserBoundStatement = removeUserStatement.bind(uuid);
        session.execute(removeUserBoundStatement);

        // Löschen des Nutzers in der Tabelle "user_by_email"
        PreparedStatement removeEmailStatement = session.prepare("DELETE FROM stackoverflow.user_by_email WHERE email = ?");
        BoundStatement removeEmailBoundStatement = removeEmailStatement.bind(email);

        ResultSet resultSet = session.execute(removeEmailBoundStatement);

        timer.stop();

        if(debug) {
            System.out.println("Deleted user " + uuid.toString() + " in " + timer);
        }

        return resultSet;
    }

    private ResultSet removeQuestion(UUID idQuestion, JsonArray tags, Timestamp createdAt) {
        timer.start();

        // Löschen der Frage in der Tabelle "question_by_tag"
        for(JsonElement tag : tags){
            PreparedStatement removeQuestionByTagStatement = session.prepare("DELETE FROM stackoverflow.questions_by_tag WHERE tagName = ? AND idQuestion = ? AND questionCreatedAt = ?");
            BoundStatement removeQuestionByTagBoundStatement = removeQuestionByTagStatement.bind(tag.getAsString(), idQuestion, createdAt);
            session.execute(removeQuestionByTagBoundStatement);
        }

        // Löschen der Frage in der Tabelle "question"
        PreparedStatement removeQuestionStatement = session.prepare("DELETE FROM stackoverflow.questions WHERE idQuestion = ?");
        BoundStatement removeQuestionBoundStatement = removeQuestionStatement.bind(idQuestion);
        session.execute(removeQuestionBoundStatement);

        // Löschen der Frage in der Tabelle "latest_questions"
        String dateString = createdAt.toString().substring(0, 10).replace("-", "");

        PreparedStatement removeLatestQuestionStatement = session.prepare("DELETE FROM stackoverflow.latest_questions WHERE yymmdd = ? AND idQuestion = ? AND createdAt = ?");
        BoundStatement removeLatestQuestionBoundStatement = removeLatestQuestionStatement.bind(dateString, idQuestion, createdAt);
        session.execute(removeLatestQuestionBoundStatement);


        ResultSet resultSet = session.execute(removeQuestionBoundStatement);
        timer.stop();

        if(debug) {
            System.out.println("Deleted question " + idQuestion.toString() + " in " + timer);
        }

        return resultSet;
    }

}
