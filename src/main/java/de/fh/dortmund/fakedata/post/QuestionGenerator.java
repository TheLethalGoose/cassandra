package de.fh.dortmund.fakedata.post;

import com.datastax.driver.core.Session;
import com.github.javafaker.Faker;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.model.User;
import de.fh.dortmund.service.POST;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionGenerator {

	static Faker faker = new Faker();
	static Timer timer = new Timer();

	public static void generateQuestions(Session session, int amount, List<User> users) {

		if(users.isEmpty()){
			System.out.println("No users found. Please create users first.");
			return;
		}

		POST POST = new POST(session, false);
		int randomIndex = (int)(Math.random() * users.size());
		int tagsToGenerate = (int)(Math.random() * 10);

		timer.start();
		for (int i = 0; i < amount; i++) {

			String title = faker.lorem().sentence();
			String body = faker.lorem().paragraph();
			String userId = users.get(randomIndex).getIdUser();
			int views = faker.number().numberBetween(0, 10000);
			int votes = faker.number().numberBetween(-100, 10000);
			Set<String> tags = new HashSet<>();
			for(int tagCount = 0; tagCount < tagsToGenerate; tagCount++) {
				tags.add(faker.hacker().noun());
			}

			POST.createQuestion(title, body, userId, tags, null, null, views, votes);

		}
		System.out.println("Created " + amount + " questions in " + timer);

	}

}
