package Workout;

import Workout.Logger.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class Bootstrap implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private LogService logger;

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class);
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        logger.debug("Checking active profiles...");
        for (String prof : event.getApplicationContext().getEnvironment().getActiveProfiles()) {
            logger.debug("Active profile: " + prof);
        }
    }

}
