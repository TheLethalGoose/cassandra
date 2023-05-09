package de.fh.dortmund.fakedata.receiver;

import com.datastax.driver.core.Session;
import de.fh.dortmund.helper.Timer;
import de.fh.dortmund.models.User;
import de.fh.dortmund.service.GET;

import java.util.List;
import java.util.Random;

import static de.fh.dortmund.helper.Statistics.calculateMedian;

public class UserReceiver {

    static Timer timer = new Timer();
    static Random random = new Random();

    public static long medianTimeToFetchUsersByUUID(Session session, List<User> users, int iterations){
        long[] times = new long[iterations];

        GET get = new GET(session, false);

        for(int i = 0; i < iterations; i++){
            User user = users.get(random.nextInt(users.size()));
            timer.start();
            get.findUser(user);
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }

    public static long medianTimeToFetchUsersByEmail(Session session, List<User> users, int iterations){
        long[] times = new long[iterations];

        GET get = new GET(session, false);

        for(int i = 0; i < iterations; i++){
            User user = users.get(random.nextInt(users.size()));
            timer.start();
            get.getUserByEmail(user.getEmail());
            long time = timer.getElapsedTime();
            times[i] = time;
        }
        return calculateMedian(times);
    }

}
