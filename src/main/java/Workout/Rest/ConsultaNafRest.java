package Workout.Rest;

import Workout.ORM.Model.ConsultaNaf;
import Workout.ORM.Model.Queue;
import Workout.ORM.QueueService;
import Workout.ORM.Repository.ConsultaNafRepository;
import Workout.ORM.Repository.ProcessTypeRepository;
import Workout.ORM.Repository.QueueRepository;
import Workout.ORM.Repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/consulta/naf")
@CrossOrigin
public class ConsultaNafRest {
    @Autowired
    private ConsultaNafRepository consultaNafRepository;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private QueueService queueService;
    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ProcessTypeRepository processTypeRepository;

    @RequestMapping(value = "/{consultaId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOp(@PathVariable long consultaId) {
        RestResponse resp = new RestResponse();

        ConsultaNaf consultaNaf = this.consultaNafRepository.findByIdOrderByDateProcessedDesc(consultaId);

        resp.setData("");
        if (consultaNaf == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        String data = "";
        if (consultaNaf.getStatus().getStatus().equals("COMPLETED")) {
            data = consultaNaf.getData();
        } else if (consultaNaf.getStatus().getStatus().equals("ERROR")) {
            data = consultaNaf.getErrMsg();
        }


        resp.setMessage(consultaNaf.getStatus().getStatus());
        resp.setData(data);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> postOp(@RequestBody ConsultaNaf consultaNaf) {
        RestResponse resp = new RestResponse();
        /*
         * Comprobar que no exista una solicitud similar y esté pendiente.
         * Si no hay ninguna, se crea una nueva y se agrega a la cola para el bot.
         * Si existe una previa, se devuelve la ID de la previa, excepto:
         * Si existe y esta en estado de error o completada, que se genera una nueva.
         */
        ConsultaNaf queueConsultaNaf = this.queueService.isConsultaNafOnQueue(consultaNaf);
        if (queueConsultaNaf != null) {
            resp.setMessage(RestResponse.Message.RETRIEVED);
            consultaNaf = queueConsultaNaf;
        } else {
            consultaNaf.setDateProcessed(LocalDateTime.now());
            consultaNaf.setStatus(this.statusRepository.findByStatus("AWAITING"));
            consultaNaf.setDateInit(LocalDateTime.now());
            this.consultaNafRepository.save(consultaNaf);

            Queue queue = new Queue();
            queue.setDateAdded(LocalDateTime.now());
            queue.setProcessType(this.processTypeRepository.findByType("CONSULTA_NAF"));
            queue.setRefId(consultaNaf.getId());
            this.queueRepository.save(queue);
            resp.setMessage(RestResponse.Message.CREATED);

        }

        /* Success */
        resp.setData(consultaNaf.getId());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}