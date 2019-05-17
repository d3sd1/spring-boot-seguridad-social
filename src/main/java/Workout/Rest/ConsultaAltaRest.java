package Workout.Rest;

import Workout.ORM.Model.ConsultaAlta;
import Workout.ORM.Model.Queue;
import Workout.ORM.QueueService;
import Workout.ORM.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/consulta/alta")
public class ConsultaAltaRest {
    @Autowired
    private ConsultaAltaRepository consultaAltaRepository;
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

        ConsultaAlta consultaAlta = this.consultaAltaRepository.findByIdOrderByDateProcessedDesc(consultaId);

        resp.setData("");
        if (consultaAlta == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        String data = "";
        if (consultaAlta.getStatus().getStatus().equals("COMPLETED")) {
            data = consultaAlta.getData();
        } else if (consultaAlta.getStatus().getStatus().equals("ERROR")) {
            data = consultaAlta.getErrMsg();
        }

        resp.setMessage(consultaAlta.getStatus().getStatus());
        resp.setData(data);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Object> postOp(@RequestBody ConsultaAlta consultaAlta) {
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
        consultaAlta.setCca(this.contractAccountRepository.findByName(consultaAlta.getCca().getName()));
        if (consultaAlta.getCca() == null) {
            resp.setMessage(RestResponse.Message.INVALID_DATE);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        /*
         * Validar fecha
         */
        if (consultaAlta.getFrc() == null) {
            resp.setMessage(RestResponse.Message.INVALID_DATE);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        ConsultaAlta queueConsultaAlta = this.queueService.isConsultaAltaOnQueue(consultaAlta);
        if (queueConsultaAlta != null) {
            resp.setMessage(RestResponse.Message.RETRIEVED);
            consultaAlta = queueConsultaAlta;
        } else {
            consultaAlta.setDateProcessed(LocalDateTime.now());
            consultaAlta.setStatus(this.statusRepository.findByStatus("AWAITING"));
            consultaAlta.setDateInit(LocalDateTime.now());
            this.consultaAltaRepository.save(consultaAlta);

            Queue queue = new Queue();
            queue.setDateAdded(LocalDateTime.now());
            queue.setProcessType(this.processTypeRepository.findByType("CONSULTA_ALTA"));
            queue.setRefId(consultaAlta.getId());
            this.queueRepository.save(queue);
            resp.setMessage(RestResponse.Message.CREATED);

        }

        /* Success */
        resp.setData(consultaAlta.getId());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}