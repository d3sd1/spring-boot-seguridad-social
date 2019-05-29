package Workout.Rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin
public class DefaultRest {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<Object> getAlta(@PathVariable long altaId) {
        RestResponse resp = new RestResponse();
        resp.setMessage("Welcome!");
        resp.setData("High d3sd1 efficient rest on the go!");
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

}
