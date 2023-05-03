package de.fh.dortmund.fakedata.generator.user;

import com.datastax.driver.core.Session;
import com.github.javafaker.Faker;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.model.User;
import de.fh.dortmund.service.POST;

import java.util.ArrayList;
import java.util.List;

public class UserGenerator{

	static Faker faker = new Faker();
	static Timer timer = new Timer();

	public static List<User> generateUsers(Session session, int amount) {

		POST POST = new POST(session, false);
		List<User> users = new ArrayList<>();

		timer.start();
		for (int i = 0; i < amount; i++) {
			String username = faker.name().username();
			String password = faker.internet().password();
			String email = faker.internet().emailAddress();
			int reputation = faker.number().numberBetween(0, 10000);

			User newUser = new User(username, password, email, reputation);
			users.add(newUser);
			POST.createUser(newUser);
		}
		System.out.println("Created " + amount + " users in " + timer);
		return users;

	}
}
