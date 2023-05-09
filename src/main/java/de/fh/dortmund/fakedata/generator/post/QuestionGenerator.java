package de.fh.dortmund.fakedata.generator.post;

import com.datastax.driver.core.Session;
import com.github.javafaker.Faker;
import de.fh.dortmund.helper.LocalDateTimeGenerator;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.Question;
import de.fh.dortmund.models.Tag;
import de.fh.dortmund.models.User;
import de.fh.dortmund.service.POST;

import java.time.LocalDateTime;
import java.util.*;

public class QuestionGenerator {

	static Faker faker = new Faker();
	static Timer timer = new Timer();
	static Random random = new Random();

	public static long generateQuestions(Session session, List<Question> questionsRef, List<User> usersRef, Set<Tag> tagsRef, int amount, int tagAmount, boolean debug) {

		if(usersRef.isEmpty()){
			System.out.println("No users found. Please create users first.");
			return -1;
		}

		List<Question> questions = new ArrayList<>();
		POST POST = new POST(session, false);

		timer.start();

		for (int i = 0; i < amount; i++) {

			int tagsToGenerate = random.nextInt(tagAmount) + 1;
			//int tagsToGenerate = tagAmount;

			int randomUserIndex = (int)(Math.random() * usersRef.size());
			Set<Tag> tagsToQuestion = new HashSet<>();

			String title = faker.lorem().sentence();
			String body = faker.lorem().paragraph();
			String userId = usersRef.get(randomUserIndex).getId();
			int views = faker.number().numberBetween(0, 10000);
			int votes = faker.number().numberBetween(-100, 10000);

			LocalDateTime createdAt = LocalDateTimeGenerator.generateRandomLocalDateTime();
			LocalDateTime modifiedAt = LocalDateTimeGenerator.generateRandomLocalDateTimeAfter(createdAt);

			for(int tagCount = 0; tagCount < tagsToGenerate; tagCount++) {

				String tagName = faker.hacker().noun();
				String tagInfo = faker.lorem().sentence();
				Tag newTag = new Tag(tagName, tagInfo);

				tagsRef.add(newTag);
				tagsToQuestion.add(newTag);
			}

			Question newQuestion = new Question(userId, title, body, createdAt.toString(), modifiedAt.toString());
			questionsRef.add(newQuestion);
			POST.createQuestion(newQuestion, tagsToQuestion, views, votes,0);

		}

		long timeToCreate = timer.getElapsedTime();

		if (debug){
			System.out.println("Created " + amount + " questions in " + timeToCreate);
		}

		return timer.getElapsedTime();

	}

}
