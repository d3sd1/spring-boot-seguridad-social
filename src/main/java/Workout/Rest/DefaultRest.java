package Workout.Rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class DefaultRest {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<Object> getAlta(@PathVariable long altaId) {
        RestResponse resp = new RestResponse();
        resp.setMessage("Welcome!");
        resp.setData("High d3sd1 efficient rest on the go!");
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

}
