package Workout.Rest;

import Workout.ORM.Model.ConsultaAltasCcc;
import Workout.ORM.Model.Queue;
import Workout.ORM.QueueService;
import Workout.ORM.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/consulta/altas")
public class ConsultaAltasCccRest {
    @Autowired
    private ConsultaAltasCccRepository consultaAltasCccRepository;
    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private ContractAccountRepository contractAccountRepository;

    @Autowired
    private QueueService queueService;
    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ProcessTypeRepository processTypeRepository;

    @RequestMapping(value = "/{consultaId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOp(@PathVariable long consultaId) {
        RestResponse resp = new RestResponse();

        ConsultaAltasCcc consultaAltasCcc = this.consultaAltasCccRepository.findByIdOrderByDateProcessedDesc(consultaId);

        resp.setData("");
        if (consultaAltasCcc == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        String data = "";
        if (consultaAltasCcc.getStatus().getStatus().equals("COMPLETED")) {
            data = consultaAltasCcc.getData();
        } else if (consultaAltasCcc.getStatus().getStatus().equals("ERROR")) {
            data = consultaAltasCcc.getErrMsg();
        }


        resp.setMessage(consultaAltasCcc.getStatus().getStatus());
        resp.setData(data);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Object> postOp(@RequestBody ConsultaAltasCcc consultaAltasCcc) {
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
        consultaAltasCcc.setCca(this.contractAccountRepository.findByName(consultaAltasCcc.getCca().getName()));
        if (consultaAltasCcc.getCca() == null) {
            resp.setMessage(RestResponse.Message.CONTRACT_ACCOUNT_NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }


        ConsultaAltasCcc queueConsultaAltasCcc = this.queueService.isConsultaCccOnQueue(consultaAltasCcc);
        if (queueConsultaAltasCcc != null) {
            resp.setMessage(RestResponse.Message.RETRIEVED);
            consultaAltasCcc = queueConsultaAltasCcc;
        } else {
            consultaAltasCcc.setDateProcessed(LocalDateTime.now());
            consultaAltasCcc.setStatus(this.statusRepository.findByStatus("AWAITING"));
            consultaAltasCcc.setDateInit(LocalDateTime.now());
            this.consultaAltasCccRepository.save(consultaAltasCcc);

            Queue queue = new Queue();
            queue.setDateAdded(LocalDateTime.now());
            queue.setProcessType(this.processTypeRepository.findByType("CONSULTA_ALTAS_CCC"));
            queue.setRefId(consultaAltasCcc.getId());
            this.queueRepository.save(queue);
            resp.setMessage(RestResponse.Message.CREATED);

        }

        /* Success */
        resp.setData(consultaAltasCcc.getId());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}