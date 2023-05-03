package de.fh.dortmund.service;

import com.datastax.driver.core.*;
import com.github.javafaker.Faker;
import com.google.gson.JsonArray;
import de.fh.dortmund.json.JsonConverter;
import de.fh.dortmund.model.Answer;
import de.fh.dortmund.model.Question;
import de.fh.dortmund.model.Tag;
import de.fh.dortmund.model.User;

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

	public JsonArray createUser(User user){

		ResultSet resultSet = createUser(UUID.fromString(user.getId()), user.getUsername(), user.getPassword(), user.getEmail(), user.getReputation());

		return JsonConverter.resultSetToJsonArray(resultSet);
	}

	public JsonArray createAnswer(Answer answer, Question question){

		ResultSet resultSet = createAnswer(UUID.fromString(answer.getId()), UUID.fromString(question.getId()), UUID.fromString(answer.getId()), LocalDateTime.parse(answer.getCreatedAt()), answer.getContent(), 0);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}
	public JsonArray createAnswer(Answer answer, Question question, int votes){

		ResultSet resultSet = createAnswer(UUID.fromString(answer.getId()), UUID.fromString(question.getId()), UUID.fromString(answer.getId()), LocalDateTime.parse(answer.getCreatedAt()), answer.getContent(), votes);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}

	public JsonArray createQuestion(Question question, Set<Tag> tags){

		ResultSet resultSet = createQuestion(UUID.fromString(question.getId()), question.getTitle(), question.getContent(), UUID.fromString(question.getUserId()), LocalDateTime.parse(question.getCreatedAt()), tags, null,  0, 0);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}
	public JsonArray createQuestion(Question question, Set<Tag> tags, Set<String> linkedQuestions) {

		ResultSet resultSet = createQuestion(UUID.fromString(question.getId()), question.getTitle(), question.getContent(), UUID.fromString(question.getUserId()), LocalDateTime.parse(question.getCreatedAt()), tags, linkedQuestions,  0, 0);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}
	public JsonArray createQuestion(Question question, Set<Tag> tags, int votes, int views) {

		LocalDateTime timestamp = LocalDateTime.parse(question.getCreatedAt());
		ResultSet resultSet = createQuestion(UUID.fromString(question.getId()), question.getTitle(), question.getContent(), UUID.fromString(question.getUserId()), timestamp, tags, null, views, votes);

		return JsonConverter.resultSetToJsonArray(resultSet);
	}

	private ResultSet createUser(UUID uuid, String username, String password, String email, int reputation) {
		timer.start();

		// Erstellen einer neuen Zeile in der Tabelle "user"
		PreparedStatement userStatement = session.prepare("INSERT INTO user (idUser, username, email, reputation) VALUES (?, ?, ?, ?)");
		BoundStatement userBoundStatement = userStatement.bind(uuid, username, email, reputation);
		session.execute(userBoundStatement);

		// Erstellen einer neuen Zeile in der Tabelle "user_by_email"
		PreparedStatement emailStatement = session.prepare("INSERT INTO user_by_email (email, password, idUser) VALUES (?, ?, ?)");
		BoundStatement emailBoundStatement = emailStatement.bind(email, password, uuid);

		ResultSet resultSet = session.execute(emailBoundStatement);

		timer.stop();

		if(debug) {
			System.out.println("Created user " + username + " in " + timer);
		}

		return resultSet;

	}

	private ResultSet createQuestion(UUID uuid, String title, String content, UUID userId, LocalDateTime createdAt, Set<Tag> tags, Set<String> linkedQuestions, int views, int votes) {
		timer.start();

		Timestamp createdAtTimeStamp = Timestamp.valueOf(createdAt);
		String dateString = createdAtTimeStamp.toString().substring(0, 10).replace("-", "");
		Set<String> tagNameSet = tags.stream().map(Tag::getName).collect(Collectors.toSet());

		Faker faker = new Faker();
		Random random = new Random();

		// Erstellen einer neuen Zeile in der Tabelle "questions"
		PreparedStatement questionStatement = session.prepare("INSERT INTO questions (idQuestion, title, content, createdAt, createdBy, lastModifiedAt, linkedQuestions, tags, views, votes, answers) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		BoundStatement questionBoundStatement = questionStatement.bind(uuid, title, content, createdAtTimeStamp, userId, createdAtTimeStamp, linkedQuestions, tagNameSet, views, votes, 0);
		ResultSet resultSet = session.execute(questionBoundStatement);

		// Erstellen einer neuen Zeile in der Tabelle "latest_questions"
		PreparedStatement latestQuestionsStatement = session.prepare("INSERT INTO latest_questions (yymmdd, createdAt, idQuestion, answers, createdBy, isAnswered, tags, title, views, votes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		BoundStatement latestQuestionsBoundStatement = latestQuestionsStatement.bind(dateString, createdAtTimeStamp, uuid, 0, userId, false, tagNameSet, title, views, votes);
		session.execute(latestQuestionsBoundStatement);

		// Erstellen einer neuen Zeile in der Tabelle "question_by_tag"
		for(Tag tag : tags) {

			String findIdToTagNameQuery = "SELECT idTag, tagInfo, tagRelatedTags, tagSynonyms FROM stackoverflow.questions_by_tag WHERE tagName = '" + tag.getName() + "'";
			ResultSet findInfosToTagNameResult = session.execute(findIdToTagNameQuery);

			PreparedStatement questionByTagStatement = session.prepare("INSERT INTO questions_by_tag (tagName, questionCreatedAt, idQuestion, idTag, questionAnswers, questionCreatedBy, questionIsAnswered, questionTitle, questionViews, questionVotes, tagInfo, tagRelatedTags, tagSynonyms) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");


			//Tag noch nicht im System
			if(findInfosToTagNameResult.isExhausted()){

				//Simuliere das finden relevanter Tags und Synonyme zu dem Tag
				Set<String> tagRelatedTags = new HashSet<>(faker.lorem().words(random.nextInt(5)+1));
				Set<String> tagSynonyms = new HashSet<>(faker.lorem().words(random.nextInt(5)+1));

				BoundStatement questionByTagBoundStatement = questionByTagStatement.bind(tag.getName(), createdAtTimeStamp, uuid, UUID.randomUUID(), 0, userId, false, title, views, votes, tag.getInfo(), tagRelatedTags, tagSynonyms);
				session.execute(questionByTagBoundStatement);
				continue;
			}

			Row findInfosToTagNameRowOne = findInfosToTagNameResult.one();

			UUID idTag = findInfosToTagNameRowOne.getUUID("idTag");
			String tagInfo = findInfosToTagNameRowOne.getString("tagInfo");
			Set<String> tagRelatedTags = findInfosToTagNameRowOne.getSet("tagRelatedTags", String.class);
			Set<String> tagSynonyms = findInfosToTagNameRowOne.getSet("tagSynonyms", String.class);

			BoundStatement questionByTagBoundStatement = questionByTagStatement.bind(tag.getName(), createdAtTimeStamp, uuid, idTag, 0, userId, false, title, views, votes, tagInfo, tagRelatedTags, tagSynonyms);
			session.execute(questionByTagBoundStatement);

		}

		if(debug) {
			System.out.println("Created question " + " in " + timer);
		}

		return resultSet;

	}

	private ResultSet createAnswer(UUID idAnswer, UUID idQuestion, UUID user, LocalDateTime createdAt, String answerText, int votes){
		timer.start();

		Timestamp createdAtTimestamp = Timestamp.valueOf(createdAt);

		PreparedStatement userStatement = session.prepare("INSERT INTO answers_by_question (idQuestion, idAnswer, createdAt, accepted, content, creator, votes) VALUES (?, ?, ?, ?, ?, ?, ?)");
		BoundStatement userBoundStatement = userStatement.bind(idQuestion, idAnswer, createdAtTimestamp, false, answerText, user, votes);
		ResultSet resultSet	= session.execute(userBoundStatement);

		timer.stop();

		if(debug) {
			System.out.println("Created answer " + idAnswer + " in " + timer);
		}

		return resultSet;

	}

}
