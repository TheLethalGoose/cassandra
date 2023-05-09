package de.fh.dortmund.service;

import com.datastax.driver.core.*;
import com.github.javafaker.Faker;
import com.google.gson.JsonArray;
import de.fh.dortmund.json.JsonConverter;
import de.fh.dortmund.models.Answer;
import de.fh.dortmund.models.Question;
import de.fh.dortmund.models.Tag;
import de.fh.dortmund.models.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class POST extends REST {

	public POST(Session session, boolean debug) {
		super(session, debug);
	}

	PreparedStatement questionStatement = session.prepare("INSERT INTO question (idQuestion, title, content, createdAt, modifiedAt, idUser, linkedQuestions, tags, views, votes, answers) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	PreparedStatement latestQuestionsStatement = session.prepare("INSERT INTO latest_questions (yymmdd, createdAt, modifiedAt, idQuestion, answers, idUser, isAnswered, tags, title, views, votes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	PreparedStatement answersByQuestionStatement = session.prepare("INSERT INTO answers_by_question (idQuestion, idAnswer, createdAt, modifiedAt, accepted, content, idUser, votes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
	PreparedStatement questionByTagStatement = session.prepare("INSERT INTO questions_by_tag (tagName, createdAt, modifiedAt, idQuestion, idTag, answers, idUser, isAnswered, title, tags, views, votes, tagInfo, tagRelatedTags, tagSynonyms) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	PreparedStatement questionsByUserStatement = session.prepare("INSERT INTO questions_by_user (idUser, createdAt, modifiedAt, idQuestion, answers, isAnswered, tags, title, views, votes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	PreparedStatement emailStatement = session.prepare("INSERT INTO user_by_email (email, password, idUser) VALUES (?, ?, ?)");
	PreparedStatement userStatement = session.prepare("INSERT INTO user (idUser, username, email, reputation) VALUES (?, ?, ?, ?)");

	public JsonArray createUser(User user){

		ResultSet resultSet = createUser(UUID.fromString(user.getId()), user.getUsername(), user.getPassword(), user.getEmail(), user.getReputation());

		return JsonConverter.resultSetToJsonArray(resultSet);
	}

	public JsonArray createAnswer(Answer answer, Question question){

		ResultSet resultSet = createAnswer(UUID.fromString(answer.getId()), UUID.fromString(question.getId()), UUID.fromString(answer.getId()), answer.getCreatedAt(), answer.getModifiedAt(), answer.isAccepted(), answer.getContent(), 0);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}
	public JsonArray createAnswer(Answer answer, UUID idQuestion, int votes){

		ResultSet resultSet = createAnswer(UUID.fromString(answer.getId()), idQuestion, UUID.fromString(answer.getId()), answer.getCreatedAt(), answer.getModifiedAt(), answer.isAccepted(), answer.getContent(), votes);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}
	public JsonArray createAnswer(Answer answer, Question question, int votes){

		ResultSet resultSet = createAnswer(UUID.fromString(answer.getId()), UUID.fromString(question.getId()), UUID.fromString(answer.getId()), answer.getCreatedAt(), answer.getModifiedAt(), answer.isAccepted(), answer.getContent(), votes);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}

	public JsonArray createQuestion(Question question, Set<Tag> tags){

		ResultSet resultSet = createQuestion(UUID.fromString(question.getId()), question.getTitle(), question.getContent(), UUID.fromString(question.getUserId()), question.getCreatedAt(), question.getModifiedAt(), tags, null,  0, 0, 0);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}
	public JsonArray createQuestion(Question question, Set<Tag> tags, Set<String> linkedQuestions) {

		ResultSet resultSet = createQuestion(UUID.fromString(question.getId()), question.getTitle(), question.getContent(), UUID.fromString(question.getUserId()), question.getCreatedAt(), question.getModifiedAt(), tags, linkedQuestions,  0, 0, 0);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}
	public JsonArray createQuestion(Question question, Set<Tag> tags, int votes, int views, int answers) {

		ResultSet resultSet = createQuestion(UUID.fromString(question.getId()), question.getTitle(), question.getContent(), UUID.fromString(question.getUserId()), question.getCreatedAt(), question.getModifiedAt(), tags, null, views, votes, answers);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}

	private ResultSet createUser(UUID uuid, String username, String password, String email, int reputation) {
		timer.start();

		// Erstellen einer neuen Zeile in der Tabelle "user"
		BoundStatement userBoundStatement = userStatement.bind(uuid, username, email, reputation);
		session.execute(userBoundStatement);

		// Erstellen einer neuen Zeile in der Tabelle "user_by_email"
		BoundStatement emailBoundStatement = emailStatement.bind(email, password, uuid);
		ResultSet resultSet = session.execute(emailBoundStatement);

		timer.stop();

		if(debug) {
			System.out.println("Created user " + username + " in " + timer);
		}

		return resultSet;

	}

	private ResultSet createQuestion(UUID uuid, String title, String content, UUID idUser, String createdAtString, String modifiedAtString, Set<Tag> tags, Set<String> linkedQuestions, int views, int votes, int answers) {
		timer.start();

		if (!createdAtString.contains("T")) {
			createdAtString = createdAtString.replace(" ", "T");
		}
		if (!modifiedAtString.contains("T")) {
			modifiedAtString = modifiedAtString.replace(" ", "T");
		}

		LocalDateTime createdAt = LocalDateTime.parse(createdAtString);
		LocalDateTime modifiedAt = LocalDateTime.parse(modifiedAtString);;

		Timestamp createdAtTimeStamp = Timestamp.valueOf(createdAt);
		Timestamp modifiedAtTimeStamp = Timestamp.valueOf(modifiedAt);

		String dateString = createdAtTimeStamp.toString().substring(0, 10).replace("-", "");
		Set<String> tagNameSet = tags.stream().map(Tag::getName).collect(Collectors.toSet());

		Faker faker = new Faker();
		Random random = new Random();

		// Erstellen einer neuen Zeile in der Tabelle "questions"
		BoundStatement questionBoundStatement = questionStatement.bind(uuid, title, content, createdAtTimeStamp, modifiedAtTimeStamp, idUser, linkedQuestions, tagNameSet, views, votes, answers);
		ResultSet resultSet = session.execute(questionBoundStatement);

		// Erstellen einer neuen Zeile in der Tabelle "latest_questions"
		BoundStatement latestQuestionsBoundStatement = latestQuestionsStatement.bind(dateString, createdAtTimeStamp, modifiedAtTimeStamp, uuid, answers, idUser, false, tagNameSet, title, views, votes);
		session.execute(latestQuestionsBoundStatement);

		// Erstellen einer neuen Zeile in der Tabelle "questions_by_user"

		BoundStatement questionsByUserBoundStatement = questionsByUserStatement.bind(idUser, createdAtTimeStamp, modifiedAtTimeStamp, uuid, answers, false, tagNameSet, title, views, votes);
		session.execute(questionsByUserBoundStatement);

		// Erstellen einer neuen Zeile in der Tabelle "question_by_tag"
		for(Tag tag : tags) {

			String findIdToTagNameQuery = "SELECT idTag, tagInfo, tagRelatedTags, tagSynonyms FROM stackoverflow.questions_by_tag WHERE tagName = '" + tag.getName() + "'";
			ResultSet findInfosToTagNameResult = session.execute(findIdToTagNameQuery);

			//Tag noch nicht im System
			if(findInfosToTagNameResult.isExhausted()){

				//Simuliere das finden relevanter Tags und Synonyme zu dem Tag
				Set<String> tagRelatedTags = new HashSet<>(faker.lorem().words(random.nextInt(5)+1));
				Set<String> tagSynonyms = new HashSet<>(faker.lorem().words(random.nextInt(5)+1));

				BoundStatement questionByTagBoundStatement = questionByTagStatement.bind(tag.getName(), createdAtTimeStamp, modifiedAtTimeStamp, uuid, UUID.randomUUID(), answers, idUser, false, title, tagNameSet, views, votes, tag.getInfo(), tagRelatedTags, tagSynonyms);
				session.execute(questionByTagBoundStatement);
				continue;
			}

			Row findInfosToTagNameRowOne = findInfosToTagNameResult.one();

			UUID idTag = findInfosToTagNameRowOne.getUUID("idTag");
			String tagInfo = findInfosToTagNameRowOne.getString("tagInfo");
			Set<String> tagRelatedTags = findInfosToTagNameRowOne.getSet("tagRelatedTags", String.class);
			Set<String> tagSynonyms = findInfosToTagNameRowOne.getSet("tagSynonyms", String.class);

			BoundStatement questionByTagBoundStatement = questionByTagStatement.bind(tag.getName(), createdAtTimeStamp, modifiedAtTimeStamp,  uuid, idTag, answers, idUser, false, title, tagNameSet, views, votes, tagInfo, tagRelatedTags, tagSynonyms);
			session.execute(questionByTagBoundStatement);

		}

		if(debug) {
			System.out.println("Created question " + " in " + timer);
		}

		return resultSet;

	}

	public ResultSet createAnswer(UUID idAnswer, UUID idQuestion, UUID user, String createdAtString, String modifiedAtString, boolean accepted, String answerText, int votes){
		timer.start();

		if (!createdAtString.contains("T")) {
			createdAtString = createdAtString.replace(" ", "T");
		}
		if (!modifiedAtString.contains("T")) {
			modifiedAtString = modifiedAtString.replace(" ", "T");
		}

		LocalDateTime createdAt = LocalDateTime.parse(createdAtString);
		LocalDateTime modifiedAt = LocalDateTime.parse(modifiedAtString);;

		Timestamp createdAtTimestamp = Timestamp.valueOf(createdAt);
		Timestamp modifiedAtTimestamp = Timestamp.valueOf(modifiedAt);

		BoundStatement userBoundStatement = answersByQuestionStatement.bind(idQuestion, idAnswer, createdAtTimestamp, modifiedAtTimestamp, accepted, answerText, user, votes);
		ResultSet resultSet	= session.execute(userBoundStatement);

		timer.stop();

		if(debug) {
			System.out.println("Created answer " + idAnswer + " in " + timer);
		}

		return resultSet;

	}

}
