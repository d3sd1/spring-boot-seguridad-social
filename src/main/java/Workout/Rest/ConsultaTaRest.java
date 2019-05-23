package Workout.Rest;

import Workout.ORM.Model.ConsultaTa;
import Workout.ORM.Model.Queue;
import Workout.ORM.QueueService;
import Workout.ORM.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/consulta/ta")
@CrossOrigin
public class ConsultaTaRest {
    @Autowired
    private ConsultaTaRepository consultaTaRepository;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private QueueService queueService;
    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ProcessTypeRepository processTypeRepository;

    @Autowired
    private ContractAccountRepository contractAccountRepository;

    @RequestMapping(value = "/{consultaId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOp(@PathVariable long consultaId) {
        RestResponse resp = new RestResponse();

        ConsultaTa consultaTa = this.consultaTaRepository.findByIdOrderByDateProcessedDesc(consultaId);

        resp.setData("");
        if (consultaTa == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        String data = "";
        if (consultaTa.getStatus().getStatus().equals("COMPLETED")) {
            data = consultaTa.getData();
        } else if (consultaTa.getStatus().getStatus().equals("ERROR")) {
            data = consultaTa.getErrMsg();
        }

        resp.setMessage(consultaTa.getStatus().getStatus());
        resp.setData(data);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Object> postOp(@RequestBody ConsultaTa consultaTa) {
        RestResponse resp = new RestResponse();
        /*
         * Comprobar que no exista una solicitud similar y esté pendiente.
         * Si no hay ninguna, se crea una nueva y se agrega a la cola para el bot.
         * Si existe una previa, se devuelve la ID de la previa, excepto:
         * Si existe y esta en estado de error o completada, que se genera una nueva.
         */


        /*
         * Validar el tipo de empresa.
         */
        consultaTa.setCca(this.contractAccountRepository.findByName(consultaTa.getCca().getName()));
        if (consultaTa.getCca() == null) {
            resp.setMessage(RestResponse.Message.INVALID_DATE);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        /*
         * Validar fecha
         */
        if (consultaTa.getFrc() == null) {
            resp.setMessage(RestResponse.Message.INVALID_DATE);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        ConsultaTa queueConsultaTa = this.queueService.isConsultaTaOnQueue(consultaTa);
        if (queueConsultaTa != null) {
            resp.setMessage(RestResponse.Message.RETRIEVED);
            consultaTa = queueConsultaTa;
        } else {
            consultaTa.setDateProcessed(LocalDateTime.now());
            consultaTa.setStatus(this.statusRepository.findByStatus("AWAITING"));
            consultaTa.setDateInit(LocalDateTime.now());
            this.consultaTaRepository.save(consultaTa);

            Queue queue = new Queue();
            queue.setDateAdded(LocalDateTime.now());
            queue.setProcessType(this.processTypeRepository.findByType("CONSULTA_TA"));
            queue.setRefId(consultaTa.getId());
            this.queueRepository.save(queue);
            resp.setMessage(RestResponse.Message.CREATED);

        }

        /* Success */
        resp.setData(consultaTa.getId());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}