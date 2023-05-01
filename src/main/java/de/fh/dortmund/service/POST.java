package de.fh.dortmund.service;

import com.datastax.driver.core.*;
import com.github.javafaker.Faker;
import de.fh.dortmund.helper.TimeStampGenerator;
import de.fh.dortmund.model.Question;
import de.fh.dortmund.model.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class POST extends REST {

	public POST(Session session, boolean debug) {
		super(session, debug);
	}


	public User createUser(String username, String password, String email, int reputation) {
		timer.start();
		UUID idUser = UUID.randomUUID();

		// Erstellen einer neuen Zeile in der Tabelle "user"
		PreparedStatement userStatement = session.prepare("INSERT INTO user (idUser, username, email, reputation) VALUES (?, ?, ?, ?)");
		BoundStatement userBoundStatement = userStatement.bind(idUser, username, email, reputation);
		session.execute(userBoundStatement);

		// Erstellen einer neuen Zeile in der Tabelle "user_by_email"
		PreparedStatement emailStatement = session.prepare("INSERT INTO user_by_email (email, password, idUser) VALUES (?, ?, ?)");
		BoundStatement emailBoundStatement = emailStatement.bind(email, password, idUser);
		session.execute(emailBoundStatement);
		timer.stop();

		if(debug) {
			System.out.println("Created user " + username + " in " + timer);
		}

		return new User(idUser.toString(), username, email);

	}

	public void createQuestion(String title, String content, String userId, Set<String> tags, Set<String> linkedQuestions, Set<String> relatedQuestions, int views, int votes) {
		timer.start();
		UUID idQuestion = UUID.randomUUID();

		Timestamp createdAt = TimeStampGenerator.generateRandom();
		String dateString = createdAt.toString().substring(0, 10).replace("-", "");

		Faker faker = new Faker();
		Random random = new Random();

		// Erstellen einer neuen Zeile in der Tabelle "question"
		PreparedStatement questionStatement = session.prepare("INSERT INTO question (idQuestion, title, content, createdAt, createdBy, lastModifiedAt, linkedQuestions, relatedQuestions, tags, views, votes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		BoundStatement questionBoundStatement = questionStatement.bind(idQuestion, title, content, createdAt, UUID.fromString(userId), createdAt, linkedQuestions, relatedQuestions, tags, views, votes);
		session.execute(questionBoundStatement);

		// Erstellen einer neuen Zeile in der Tabelle "latest_questions"
		PreparedStatement latestQuestionsStatement = session.prepare("INSERT INTO latest_questions (yymmdd, createdAt, idQuestion, answers, createdBy, isAnswered, tags, title, views, votes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		BoundStatement latestQuestionsBoundStatement = latestQuestionsStatement.bind(dateString, createdAt, idQuestion, 0, UUID.fromString(userId), false, tags, title, views, votes);
		session.execute(latestQuestionsBoundStatement);

		// Erstellen einer neuen Zeile in der Tabelle "question_by_tag"

		for(String tag : tags) {

			String findIdToTagNameQuery = "SELECT idTag, tagInfo, tagRelatedTags, tagSynonyms FROM stackoverflow.questions_by_tag WHERE tagName = '" + tag + "'";
			ResultSet findInfosToTagNameResult = session.execute(findIdToTagNameQuery);

			PreparedStatement questionByTagStatement = session.prepare("INSERT INTO questions_by_tag (tagName, questionCreatedAt, idQuestion, idTag, questionAnswers, questionCreatedBy, questionIsAnswered, questionTitle, questionViews, questionVotes, tagInfo, tagRelatedTags, tagSynonyms) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			//Tag noch nicht im System
			if(findInfosToTagNameResult.isExhausted()){

				String tagInfo = faker.lorem().sentence();
				Set<String> tagRelatedTags = new HashSet<>(faker.lorem().words(random.nextInt(5)+1));
				Set<String> tagSynonyms = new HashSet<>(faker.lorem().words(random.nextInt(5)+1));

				BoundStatement questionByTagBoundStatement = questionByTagStatement.bind(tag, createdAt, idQuestion, UUID.randomUUID(), 0, UUID.fromString(userId), false, title, views, votes, tagInfo, tagRelatedTags, tagSynonyms);
				session.execute(questionByTagBoundStatement);
				break;
			}

			Row findInfosToTagNameRowOne = findInfosToTagNameResult.one();

			UUID idTag = findInfosToTagNameRowOne.getUUID("idTag");
			String tagInfo = findInfosToTagNameRowOne.getString("tagInfo");
			Set<String> tagRelatedTags = findInfosToTagNameRowOne.getSet("tagRelatedTags", String.class);
			Set<String> tagSynonyms = findInfosToTagNameRowOne.getSet("tagSynonyms", String.class);

			BoundStatement questionByTagBoundStatement = questionByTagStatement.bind(tag, createdAt, idQuestion, idTag, 0, UUID.fromString(userId), false, title, views, votes, tagInfo, tagRelatedTags, tagSynonyms);
			session.execute(questionByTagBoundStatement);

		}

		if(debug) {
			System.out.println("Created question " + " in " + timer);
		}

	}


}
