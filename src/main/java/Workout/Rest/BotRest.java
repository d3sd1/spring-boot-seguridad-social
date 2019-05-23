package Workout.Rest;

import Workout.Logger.LogRepository;
import Workout.ORM.Repository.QueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bot")
@CrossOrigin
public class BotRest {
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private QueueRepository queueRepository;

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<Object> status() {
        RestResponse resp = new RestResponse();
        resp.setMessage("Welcome!");
        resp.setData("High d3sd1 efficient rest on the go!");
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
    @RequestMapping(value = "/logs/internal", method = RequestMethod.GET)
    public ResponseEntity<Object> logs() {
        RestResponse resp = new RestResponse();
        resp.setMessage("LOGS");
        resp.setData(this.logRepository.findAllByOrderByObjectIdDesc());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
    @RequestMapping(value = "/queue", method = RequestMethod.GET)
    public ResponseEntity<Object> queue() {
        RestResponse resp = new RestResponse();
        resp.setMessage("QUEUE");
        resp.setData(this.queueRepository.findAllByOrderByIdDesc());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
