package Workout.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service("LogService")
@Transactional
public class LogService {
    @Autowired
    public JavaMailSender emailSender;
    private LogRepository logRepository;
    @Value("${spring.profiles.active}")
    private String activeProfile;
    @Value("${mail.mail.list.notifier}")
    private String mailNotifierList;

    @Autowired
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    private Class<?> getCaller() {
        try {
            Class<?> clazz = this.getClass();
            for (StackTraceElement trace : Thread.currentThread().getStackTrace()) {
                if (trace.getClassName().indexOf("Workout.") != -1 && trace.getClassName().indexOf("Workout.Logger") == -1) {
                    clazz = Class.forName(trace.getClassName());
                    break;
                }
            }
            return clazz;
        } catch (Exception e) {
            Logger consoleLogger = LoggerFactory.getLogger(this.getClass());
            consoleLogger.error("CRITICAL ERROR ON LOG SERVICE!!! CHECK BELOW");
            e.printStackTrace();
            return this.getClass();
        }
    }

    public void debug(String msg) {
        Logger consoleLogger = LoggerFactory.getLogger(this.getCaller());
        consoleLogger.info(msg);
    }

    public void info(String msg) {
        Logger consoleLogger = LoggerFactory.getLogger(this.getCaller());
        consoleLogger.info(msg);
        Log log = new Log();
        log.setLevel(LogLevel.INFO);
        log.setText(msg);
        log.setDate(LocalDateTime.now());
        this.logRepository.save(log);
    }

    public void warning(String msg) {
        Logger consoleLogger = LoggerFactory.getLogger(this.getCaller());
        this.sendErrorMail(msg);
        consoleLogger.warn(msg);
        Log log = new Log();
        log.setLevel(LogLevel.WARNING);
        log.setText(msg);
        log.setDate(LocalDateTime.now());
        this.logRepository.save(log);
    }

    public void error(String msg) {
        Logger consoleLogger = LoggerFactory.getLogger(this.getCaller());
        this.sendErrorMail(msg);
        consoleLogger.error(msg);
        Log log = new Log();
        log.setLevel(LogLevel.ERROR);
        log.setText(msg);
        log.setDate(LocalDateTime.now());
        this.logRepository.save(log);
    }

    private void sendErrorMail(String msg) {
        if (this.activeProfile.equals("prod")) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(this.mailNotifierList.split(","));
            message.setSubject("prueba");
            message.setText(msg);
            this.emailSender.send(message);
        }
    }

}
