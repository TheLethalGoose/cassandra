package de.fh.dortmund.service;

import com.datastax.driver.core.*;
import de.fh.dortmund.helper.TimeStampGenerator;
import de.fh.dortmund.model.Question;
import de.fh.dortmund.model.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

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
			//TODO
		}

		if(debug) {
			System.out.println("Created question " + " in " + timer);
		}

	}


}
