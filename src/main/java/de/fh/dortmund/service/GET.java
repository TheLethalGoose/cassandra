package de.fh.dortmund.service;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.google.gson.JsonArray;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.json.JsonConverter;
import de.fh.dortmund.models.Post;
import de.fh.dortmund.models.Question;
import de.fh.dortmund.models.Tag;
import de.fh.dortmund.models.User;
import de.fh.dortmund.models.enums.PostType;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class GET extends REST {

    Timer timer = new Timer();

    public GET(Session session, boolean debug) {
        super(session, debug);
    }

    PreparedStatement getTagsFromQuestionStatement = session.prepare("SELECT tags FROM stackoverflow.question WHERE idQuestion = ?");
    PreparedStatement getQuestionsByTagStatement = session.prepare("SELECT * FROM stackoverflow.questions_by_tag WHERE tagName = ?");
    PreparedStatement getQuestionsByUserStatement = session.prepare("SELECT * FROM stackoverflow.questions_by_user WHERE idUser = ?");
    PreparedStatement getAnswersByQuestionStatement = session.prepare("SELECT * FROM stackoverflow.answers_by_question WHERE idQuestion = ?");
    PreparedStatement getQuestionByDateStatement = session.prepare("SELECT * FROM stackoverflow.latest_questions WHERE yymmdd = ?");
    PreparedStatement getUserByEmailStatement = session.prepare("SELECT * FROM stackoverflow.user_by_email WHERE email = ?");
    PreparedStatement findUserStatement = session.prepare("SELECT * FROM stackoverflow.user WHERE idUser = ?");

    public JsonArray getTagsFromQuestion(Question question){
        return getTagsFromQuestion(UUID.fromString(question.getId()));
    }
    public JsonArray getTagsFromQuestion(UUID idQuestion){
        timer.start();

        BoundStatement getTagsFromQuestionStatemenBoundStatement = getTagsFromQuestionStatement.bind(idQuestion);
        ResultSet resultSet = session.execute(getTagsFromQuestionStatemenBoundStatement);


        if(debug) {
            System.out.println("Query getTagsFromQuestion completed in: " + timer);
        }

        //Object with Array in Array
        return JsonConverter.resultSetToJsonArray(resultSet).get(0).getAsJsonObject().get("tags").getAsJsonArray();
    }

    public JsonArray getQuestionsByTag(Tag tag){
        return getQuestionsByTag(tag.getName());
    }
    public JsonArray getQuestionsByTag(String tagName){
        timer.start();

        BoundStatement getQuestionsByTagBoundStatement = getQuestionsByTagStatement.bind(tagName);
        ResultSet resultSet = session.execute(getQuestionsByTagBoundStatement);

        if(debug) {
            System.out.println("Query getQuestionsByTag completed in: " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);
    }
    public JsonArray getQuestionsByUser(User user){
        return getQuestionsByUser(UUID.fromString(user.getId()));
    }
    public JsonArray getQuestionsByUser(UUID uuid){
        timer.start();

        BoundStatement getQuestionsByUserBoundStatement = getQuestionsByUserStatement.bind(uuid);
        ResultSet resultSet = session.execute(getQuestionsByUserBoundStatement);

        if(debug) {
            System.out.println("Query getQuestionsByUser completed in: " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);
    }

    public JsonArray getAnswersByQuestion(Question question){

        return getAnswersByQuestion(UUID.fromString(question.getId()));
    }
    public JsonArray getAnswersByQuestion(UUID idQuestion){
        timer.start();

        BoundStatement getAnswersByQuestionBoundStatement = getAnswersByQuestionStatement.bind(idQuestion);
        ResultSet resultSet = session.execute(getAnswersByQuestionBoundStatement);

        if(debug) {
            System.out.println("Query getAnswersByQuestion completed in: " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);
    }

    public JsonArray getQuestionByDate(String dateString){
        return getQuestionByDate(LocalDateTime.parse(dateString));
    }
    public JsonArray getLatestQuestions(){
        return getQuestionByDate(LocalDateTime.now());
    }
    public JsonArray getQuestionByDate(LocalDateTime date){
        timer.start();

        String dateString = date.toString().substring(0, 10).replace("-", "");

        BoundStatement getQuestionByDateBoundStatement = getQuestionByDateStatement.bind(dateString);
        ResultSet resultSet = session.execute(getQuestionByDateBoundStatement);

        if(debug) {
            System.out.println("Query getQuestionByDate completed in: " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);
    }

    public JsonArray getValuesFromPost(Post post, Set<String> columnNames){

        if(post.getPostType() == PostType.ANSWER){
            return getValuesFromAnswer(UUID.fromString(post.getId()), UUID.fromString(post.getParentPostId()), columnNames);
        }
        if(post.getPostType()  == PostType.QUESTION){
            return getValuesFromQuestion(UUID.fromString(post.getId()), columnNames);
        }
        return null;
    }
    public JsonArray getValuesFromAnswer(UUID idAnswer, UUID idParentPost, Set<String> columnNames){
        timer.start();

        String getTotalValuesToAnswerQuery = "SELECT " + String.join(", ", columnNames) +  " FROM stackoverflow.answers_by_question WHERE idAnswer = " + idAnswer + " AND idQuestion = " + idParentPost;
        ResultSet resultSet = session.execute(getTotalValuesToAnswerQuery);

        if(debug) {
            System.out.println("Query getTotalValuesFromQuestion completed in: " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);
    }
    public JsonArray getValuesFromQuestion(UUID idPost, Set<String> columnNames){
        timer.start();

        String getTotalValuesToQuestionQuery = "SELECT " + String.join(", ", columnNames) +  " FROM stackoverflow.question WHERE idQuestion = " + idPost;
        ResultSet resultSet = session.execute(getTotalValuesToQuestionQuery);

        if(debug) {
            System.out.println("Query getTotalValuesFromQuestion completed in: " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);
    }

    public JsonArray getUserByEmail(String email){
        timer.start();

        BoundStatement getUserByEmailBoundStatement = getUserByEmailStatement.bind(email);
        ResultSet resultSet = session.execute(getUserByEmailBoundStatement);

        if(debug) {
            System.out.println("Query getUserByEmail completed in: " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);
    }
    public JsonArray findUser(User user){
        return findUser(UUID.fromString(user.getId()));
    }
    public JsonArray findUser(UUID uuid){
        timer.start();

        BoundStatement findUserBoundStatement = findUserStatement.bind(uuid);
        ResultSet resultSet = session.execute(findUserBoundStatement);

        if(debug) {
            System.out.println("Query findUser completed in: " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);
    }

}
