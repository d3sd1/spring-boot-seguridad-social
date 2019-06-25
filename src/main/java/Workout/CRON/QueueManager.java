package Workout.CRON;

import Workout.Logger.LogService;
import Workout.ORM.Model.Operation;
import Workout.ORM.Model.Queue;
import Workout.ORM.QueueService;
import Workout.ORM.Repository.ProcessStatusRepository;
import Workout.ORM.Repository.ProcessTypeRepository;
import Workout.ORM.Repository.QueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

@Component
public class QueueManager {
    @Autowired
    private LogService logger;

    @Autowired
    private ProcessStatusRepository processStatusRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private ProcessTypeRepository processTypeRepository;


    @Value("${bot.operations.timeout.seconds}")
    private int operationTimeout;
    @Value("${bot.screenshoots.path}")
    private String screenshootPath;
    @Value("${spring.profiles.active}")
    private String envProfile;

    @Autowired
    private QueueService queueService;

    //@Async decomentar esto para que sea asincrono. modo secuencial activado por servidor low-elo
    @Scheduled(fixedRate = 3000, initialDelay = 5000)
    public void processQueue() throws InterruptedException {

        Thread.sleep((long) ((Math.random() * ((5000 - 1000) + 1)) + 1000));
        List<Queue> queue = this.queueRepository.findAll();
        this.logger.debug("Checking for new operations...");

        HashMap<String, Object> config = new HashMap<>();
        config.put("logger", this.logger);
        config.put("envProfile", this.envProfile);
        config.put("operationTimeout", this.operationTimeout);
        config.put("screenshootPath", this.screenshootPath);
        config.put("queueService", this.queueService);
        config.put("queueRepository", this.queueRepository);
        config.put("processTypeRepository", this.processTypeRepository);
        config.put("processStatusRepository", this.processStatusRepository);

        for (Queue process : queue) {
            Operation op = this.queueService.getOpFromQueue(process);
            if (op.getStatus().getStatus().equalsIgnoreCase("AWAITING")) {
                this.logger.debug("Proccesing operation " + process.getRefId() + " of type " + op.getClass().getSimpleName());
                // Status must be set here for prevent ghosting.
                op.setStatus(this.processStatusRepository.findByStatus("IN_PROCESS"));
                this.queueService.saveOp(op);
                try {
                    Class<?> clazz = Class.forName("Workout.Selenium." + op.getClass().getSimpleName() + "Bot");
                    Constructor<?> constructor = clazz.getConstructor(Operation.class, HashMap.class);
                    Object instance = constructor.newInstance(op, config);
                } catch (Exception e) {
                    e.printStackTrace();
                    this.logger.error("Class not found for queue reflection: " + e.getMessage());
                }
            }
        }
    }

    @PostConstruct
    public void prepareQueue() {
        List<Queue> queue = this.queueRepository.findAll();
        for (Queue process : queue) {
            /*
            Reset queue after loading so everything IN_PROGRESS turns to AWAIOTING.
            Implement here ABORTED if needed.
             */
            Operation op = this.queueService.getOpFromQueue(process);
            if (op.getStatus().getStatus().equalsIgnoreCase("IN_PROCESS")) {
                this.logger.debug("Reparing from IN_PROGRESS to AWAITING for " + process.getRefId() + " of type " + op.getClass().getSimpleName());
                op.setStatus(this.processStatusRepository.findByStatus("AWAITING"));
            }
            this.queueService.saveOp(op);
        }
    }
}