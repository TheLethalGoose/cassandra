package de.fh.dortmund.fakedata.generator.post;

import com.datastax.driver.core.Session;
import com.github.javafaker.Faker;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.model.Question;
import de.fh.dortmund.model.Tag;
import de.fh.dortmund.model.User;
import de.fh.dortmund.service.POST;

import java.util.*;

public class QuestionGenerator {

	static Faker faker = new Faker();
	static Timer timer = new Timer();
	static Random random = new Random();

	public static List<Question> generateQuestions(Session session, int amount, List<User> users, Set<Tag> tags) {

		if(users.isEmpty()){
			System.out.println("No users found. Please create users first.");
			return null;
		}

		List<Question> questions = new ArrayList<>();
		POST POST = new POST(session, false);
		int randomIndex = (int)(Math.random() * users.size());

		timer.start();
		for (int i = 0; i < amount; i++) {

			int tagsToGenerate = random.nextInt(10) + 1;
			Set<Tag> tagsToQuestion = new HashSet<>();

			String title = faker.lorem().sentence();
			String body = faker.lorem().paragraph();
			String userId = users.get(randomIndex).getId();
			int views = faker.number().numberBetween(0, 10000);
			int votes = faker.number().numberBetween(-100, 10000);

			for(int tagCount = 0; tagCount < tagsToGenerate; tagCount++) {

				String tagName = faker.hacker().noun();
				String tagInfo = faker.lorem().sentence();
				Tag newTag = new Tag(tagName, tagInfo);

				tags.add(newTag);
				tagsToQuestion.add(newTag);
			}

			Question newQuestion = new Question(userId, title, body);
			questions.add(newQuestion);
			POST.createQuestion(newQuestion, tagsToQuestion, views, votes);

		}
		System.out.println("Created " + amount + " questions in " + timer);

		return questions;

	}

}
