package Workout.CRON;

import Workout.Logger.LogService;
import Workout.ORM.Model.Alta;
import Workout.ORM.Repository.ProcessStatusRepository;
import Workout.Selenium.AltaBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QueueManager {
    @Autowired
    private LogService logger;
    @Value("${bot.operations.timeout.seconds}")
    private int operationTimeout;
    @Value("${bot.screenshoots.path}")
    private String screenshootPath;
    @Value("${spring.profiles.active}")
    private String envProfile;

    @Autowired
    private ProcessStatusRepository processRepository;

    @Autowired
    private ProcessStatusRepository altaRepositroy;

    @Scheduled(fixedRate = 50000)
    @Async
    public void loadQueue() {
        this.logger.info("Checking for new operations...");
        // TEST ALTA
        Alta op = new Alta();
        op.setGco("08");
        op.setIpf("53457069D");
        op.setIpt("01");
        op.setNaf("321381672738");
        new AltaBot(this.logger, op, envProfile, operationTimeout, screenshootPath, processRepository);
    }
}