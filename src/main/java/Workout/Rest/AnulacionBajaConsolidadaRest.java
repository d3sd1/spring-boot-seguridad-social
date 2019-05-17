package Workout.Rest;

import Workout.ORM.Model.AnulacionBajaConsolidada;
import Workout.ORM.Model.Queue;
import Workout.ORM.QueueService;
import Workout.ORM.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/baja/anulacion/consolidada")
public class AnulacionBajaConsolidadaRest {

    @Autowired
    private ContractAccountRepository contractAccountRepository;

    @Autowired
    private AnulacionBajaConsolidadaRepository anulacionBajaConsolidadaRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ProcessTypeRepository processTypeRepository;

    @Autowired
    private QueueService queueService;

    @RequestMapping(value = "/{bajaId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOp(@PathVariable long bajaId) {
        RestResponse resp = new RestResponse();

        AnulacionBajaConsolidada altaConsolidada = this.anulacionBajaConsolidadaRepository.findByIdOrderByDateProcessedDesc(bajaId);

        resp.setData("");
        if (altaConsolidada == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        resp.setMessage(altaConsolidada.getStatus().getStatus());
        resp.setData(altaConsolidada.getErrMsg());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "/{bajaId}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delOp(@PathVariable long bajaId) {
        RestResponse resp = new RestResponse();

        AnulacionBajaConsolidada altaPrevia = this.anulacionBajaConsolidadaRepository.findByIdOrderByDateProcessedDesc(bajaId);
        boolean success = false;
        if (altaPrevia == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        if (altaPrevia.getStatus().getStatus().equals("AWAITING") || altaPrevia.getStatus().getStatus().equals("STOPPED")) {
            altaPrevia.setStatus(this.statusRepository.findByStatus("REMOVED"));
            altaPrevia.setProcessTime(0);
            this.anulacionBajaConsolidadaRepository.delete(altaPrevia);
            success = true;
        }
        resp.setData(success);
        resp.setMessage("DELETE_STATUS");
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> postAlta(@RequestBody AnulacionBajaConsolidada alta) {
        RestResponse resp = new RestResponse();

        /*
         * Validar el tipo de empresa.
         */
        alta.setCca(this.contractAccountRepository.findByName(alta.getCca().getName()));
        if (alta.getCca() == null) {
            resp.setMessage(RestResponse.Message.CONTRACT_ACCOUNT_NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        alta.setDateInit(LocalDateTime.now());
        alta.setProcessTime(0);

        /*
         * El alta no puede sobrepasar 60 días posteriores a la actual.
         * Además, la fecha no puede ser anterior a la actual.
         */
        if (LocalDate.now().plus(60, ChronoUnit.DAYS).isBefore(alta.getFrb())) {
            resp.setMessage(RestResponse.Message.DATE_EXPIRE_INVALID);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        if (alta.getFrb().isBefore(LocalDate.now().plus(3, ChronoUnit.DAYS))) {
            resp.setMessage(RestResponse.Message.DATE_PASSED);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        AnulacionBajaConsolidada queueAlta = this.queueService.isAnulacionBajaConsolidadaOnQueue(alta);
        if (queueAlta != null) {
            resp.setMessage(RestResponse.Message.RETRIEVED);
            alta = queueAlta;
        } else {
            alta.setDateProcessed(LocalDateTime.now());
            alta.setStatus(this.statusRepository.findByStatus("AWAITING"));
            this.anulacionBajaConsolidadaRepository.save(alta);

            Queue queue = new Queue();
            queue.setDateAdded(LocalDateTime.now());
            queue.setProcessType(this.processTypeRepository.findByType("ANULACION_BAJA_CONSOLIDADA"));
            queue.setRefId(alta.getId());
            this.queueRepository.save(queue);
            resp.setMessage(RestResponse.Message.CREATED);

        }

        /* Success */
        resp.setData(alta.getId());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
