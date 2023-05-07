package de.fh.dortmund.fakedata.destroyer.user;

import com.datastax.driver.core.Session;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.User;
import de.fh.dortmund.service.DELETE;

import java.util.List;
import java.util.Random;

public class UserDestroyer {

    static Timer timer = new Timer();
    static Random random = new Random();

    public static void destroyUsers(Session session, List<User> users, int amount) {

        if(users.isEmpty() || users.size() < amount) {
            System.out.println("No users found or amount to big. Please create users first.");
            return;
        }

        DELETE DELETE = new DELETE(session, false);
        timer.start();

        for (int i = 0; i < amount; i++) {
            int indexToRemove = random.nextInt(users.size());
            User victim = users.remove(indexToRemove);
            DELETE.removeUser(victim);
        }

        System.out.println("Removed " + amount + " users in " + timer);

    }
}
