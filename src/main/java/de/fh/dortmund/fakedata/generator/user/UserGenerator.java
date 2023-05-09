package de.fh.dortmund.fakedata.generator.user;

import com.datastax.driver.core.Session;
import com.github.javafaker.Faker;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.User;
import de.fh.dortmund.service.POST;

import java.util.List;

public class UserGenerator{

	static Faker faker = new Faker();
	static Timer timer = new Timer();

	public static long generateUsers(Session session,List<User> usersRef, int amount, boolean debug) {

		POST POST = new POST(session, false);

		timer.start();

		for (int i = 0; i < amount; i++) {
			String username = faker.name().username();
			String password = faker.internet().password();
			String email = faker.internet().emailAddress();
			int reputation = faker.number().numberBetween(0, 10000);

			User newUser = new User(username, password, email, reputation);
			usersRef.add(newUser);
			POST.createUser(newUser);
		}

		long timeToCreate = timer.getElapsedTime();

		if(debug) {
			System.out.println("Created " + amount + " users in " + timeToCreate);
		}

		return timer.getElapsedTime();

	}
}
