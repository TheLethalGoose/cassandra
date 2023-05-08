package de.fh.dortmund.service;

import com.datastax.driver.core.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.fh.dortmund.json.JsonConverter;
import de.fh.dortmund.models.*;
import de.fh.dortmund.models.enums.VoteType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PUT extends REST{
    public PUT(Session session, boolean debug) {
        super(session, debug);
    }

    PreparedStatement editQuestionPreparedStatement = session.prepare("UPDATE stackoverflow.question SET content = ? WHERE idQuestion = ?");

    public JsonArray editQuestion(Question question, String alteredContent){
        return editQuestion(UUID.fromString(question.getId()), alteredContent);
    }
    public JsonArray editQuestion(UUID idQuestion, String alteredContent){

        timer.start();

        BoundStatement editQuestionBoundStatement = editQuestionPreparedStatement.bind(alteredContent, idQuestion);
        ResultSet resultSet = session.execute(editQuestionBoundStatement);

        if (debug) {
            System.out.println("Edited question in " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);

    }

    public JsonArray markAnswerAsAccepted(Answer answer){

        if(answer.isAccepted()){
            if(debug) {
                System.out.println("Answer is already accepted");
            }
            return new JsonArray();
        }

        answer.setAccepted(true);
        return markAnswerAsAccepted(UUID.fromString(answer.getId()), UUID.fromString(answer.getParentPostId()), UUID.fromString(answer.getUserId()), answer.getCreatedAt(), answer.getModifiedAt(), answer.isAccepted(), answer.getContent());
    }
    public JsonArray markAnswerAsAccepted(UUID idAnswer, UUID idParentPost, UUID user, String createdAtString, String modifiedAtString, boolean accepted, String answerText){

        DELETE DELETE = new DELETE(session, false);
        POST POST = new POST(session, false);
        GET GET = new GET(session, false);

        timer.start();

        int votesToAnswer = GET.getValuesFromAnswer(idAnswer, idParentPost, Collections.singleton("votes")).get(0).getAsJsonObject().get("votes").getAsInt();
        DELETE.removeAnswer(idAnswer, idParentPost);
        ResultSet resultSet = POST.createAnswer(idAnswer,idParentPost,user, createdAtString, modifiedAtString, accepted , answerText, votesToAnswer);

        if (debug) {
            System.out.println("Marked answer as accepted in " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);
    }
    public JsonArray vote(Post post, Vote vote){
        return vote(post, vote.getVoteType());
    }

    public JsonArray vote(Post post, VoteType voteType){

        if(voteType == VoteType.UPVOTE) {
            return increaseValueToPostUPDATE(post, "votes", 1);
        }
        else if(voteType == VoteType.DOWNVOTE){
            return increaseValueToPostUPDATE(post, "votes", -1);
        }
        else{
            return null;
        }
    }

    public JsonArray increaseValueToPostUPDATE(Post post, String columnName, int byValue){

        GET GET = new GET(session, false);

        timer.start();

        int increasedValue = GET.getValuesFromPost(post, Collections.singleton(columnName)).get(0).getAsJsonObject().get(columnName).getAsInt() + byValue;
        JsonArray tags = GET.getTagsFromQuestion(UUID.fromString(post.getId()));
        Timestamp createdAtTimeStamp = Timestamp.valueOf(LocalDateTime.parse(post.getCreatedAt()));

        String dateString = createdAtTimeStamp.toString().substring(0, 10).replace("-", "");

        // Ändern von Values in der Tabelle "questions_by_tag
        for(JsonElement tag : tags){
            String increaseValueInQuestionsByTag = "UPDATE stackoverflow.questions_by_tag SET " + columnName + " = ? WHERE tagName = ? AND idQuestion = ? AND createdAt = ?";
            PreparedStatement preparedStatementIncreaseValueInQuestionsByTag = session.prepare(increaseValueInQuestionsByTag);
            BoundStatement boundStatementIncreaseValueInQuestionsByTag = preparedStatementIncreaseValueInQuestionsByTag.bind(increasedValue, tag.getAsString(), UUID.fromString(post.getId()), Timestamp.valueOf(LocalDateTime.parse(post.getCreatedAt())));
            session.execute(boundStatementIncreaseValueInQuestionsByTag);
        }

        // Ändern von Values in der Tabelle "questions"
        String increaseValueInQuestion = "UPDATE stackoverflow.question SET " + columnName + " = ? WHERE idQuestion = ?";
        String increaseValueInQuestionByUser = "UPDATE stackoverflow.questions_by_user SET " + columnName + " = ? WHERE idUser = ? AND idQuestion = ? AND createdAt = ?";
        String increaseValueInLatestQuestion = "UPDATE stackoverflow.latest_questions SET " + columnName + " = ? WHERE yymmdd = ? AND idQuestion = ? AND createdAt = ?";

        PreparedStatement preparedStatementIncreaseValueInQuestion = session.prepare(increaseValueInQuestion);
        PreparedStatement preparedStatementIncreaseValueInQuestionByUser = session.prepare(increaseValueInQuestionByUser);
        PreparedStatement preparedStatementIncreaseValueInLatestQuestion = session.prepare(increaseValueInLatestQuestion);

        BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);

        batch.add(preparedStatementIncreaseValueInQuestion.bind(increasedValue, UUID.fromString(post.getId())));
        batch.add(preparedStatementIncreaseValueInQuestionByUser.bind(increasedValue, UUID.fromString(post.getUserId()), UUID.fromString(post.getId()), createdAtTimeStamp));
        batch.add(preparedStatementIncreaseValueInLatestQuestion.bind(increasedValue, dateString, UUID.fromString(post.getId()), createdAtTimeStamp));

        ResultSet resultSet = session.execute(batch);


        if(debug){
            System.out.println("Using UPDATE: Increased " + columnName + " to question by " + byValue + " in " + timer);
        }

        return JsonConverter.resultSetToJsonArray(resultSet);

    }

    public JsonArray increaseValueToPostINSERT(Question question, String columnName, int byValue){

        GET GET = new GET(session, false);
        POST POST = new POST(session, false);

        timer.start();

        JsonArray tags = GET.getTagsFromQuestion(UUID.fromString(question.getId()));
        JsonArray values = GET.getValuesFromPost(question, Set.of("votes", "answers", "views"));
        Set<Tag> tagsSet = tags.asList().stream().map(tag -> new Tag(tag.getAsString(),"")).collect(Collectors.toSet());

        int votes = values.get(0).getAsJsonObject().get("votes").getAsInt();
        int answers = values.get(0).getAsJsonObject().get("answers").getAsInt();
        int views = values.get(0).getAsJsonObject().get("views").getAsInt();

        JsonArray jsonArray = null;

        if(columnName.equals("answers")){
            jsonArray = POST.createQuestion(question, tagsSet, votes, views, answers + byValue);
        }
        if(columnName.equals("views")){
            jsonArray = POST.createQuestion(question, tagsSet, votes, views + byValue, answers);
        }
        if(columnName.equals("votes")) {
            jsonArray = POST.createQuestion(question, tagsSet, votes + byValue, views, answers);
        }

        if(debug){
            System.out.println("Using INSERT: Increased answers to question by " + byValue + " in " + timer);
        }

        return jsonArray;

    }

}
