package Workout.Rest;

import Workout.ORM.Model.Baja;
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
@RequestMapping("/baja")
public class BajaRest {

    @Autowired
    private ContractKeyRepository contractKeyRepository;

    @Autowired
    private ContractCoefficientRepository contractCoeRepository;

    @Autowired
    private ContractAccountRepository contractAccountRepository;

    @Autowired
    private BajaRepository bajaRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ProcessTypeRepository processTypeRepository;

    @Autowired
    private QueueService queueService;

    @RequestMapping(value = "/{bajaId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getBaja(@PathVariable long bajaId) {
        RestResponse resp = new RestResponse();

        Baja baja = this.bajaRepository.findByIdOrderByDateProcessedDesc(bajaId);

        resp.setData("");
        if (baja == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        resp.setMessage(baja.getStatus().getStatus());
        resp.setData(baja.getErrMsg());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "/{bajaId}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delBaja(@PathVariable long bajaId) {
        RestResponse resp = new RestResponse();

        Baja baja = this.bajaRepository.findByIdOrderByDateProcessedDesc(bajaId);
        boolean success = false;
        if (baja == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        if (baja.getStatus().getStatus().equals("AWAITING") || baja.getStatus().getStatus().equals("STOPPED")) {
            baja.setStatus(this.statusRepository.findByStatus("REMOVED"));
            baja.setProcessTime(0);
            this.bajaRepository.delete(baja);
            success = true;
        }
        resp.setData(success);
        resp.setMessage("DELETE_STATUS");
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> postBaja(@RequestBody Baja baja) {
        RestResponse resp = new RestResponse();

        /*
         * Validar el tipo de empresa.
         */
        baja.setCca(this.contractAccountRepository.findByName(baja.getCca().getName()));
        if (baja.getCca() == null) {
            resp.setMessage(RestResponse.Message.CONTRACT_ACCOUNT_NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        baja.setDateInit(LocalDateTime.now());
        baja.setProcessTime(0);

        if (baja.getIpt().equals(6)) {
            baja.setIpf("0" + baja.getIpf());
        }
        /*
         * Validar situación. Si no introdujo nada, el valor por defecto es 01.
         */
        if (baja.getSit() == null) {
            baja.setSit("93");
        }

        /*
         * La baja no puede sobrepasar 60 días posteriores a la actual.
         * Además, la fecha no puede ser anterior a la actual.
         */
        if (LocalDate.now().plus(60, ChronoUnit.DAYS).isBefore(baja.getFrb())) {
            resp.setMessage(RestResponse.Message.DATE_EXPIRE_INVALID);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        if (LocalDate.now().plus(3, ChronoUnit.DAYS).isBefore(baja.getFrb())) {
            resp.setMessage(RestResponse.Message.DATE_PASSED);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        if (baja.getFrb() != null && baja.getFfv().isBefore(LocalDate.now())) {
            resp.setMessage(RestResponse.Message.DATE_EXPIRE_INVALID);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        if (baja.getFfv() != null && baja.getFfv().isBefore(LocalDate.now())) {
            resp.setMessage(RestResponse.Message.DATE_EXPIRE_INVALID);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }


        Baja queueBaja = this.queueService.isBajaOnQueue(baja);
        if (queueBaja != null) {
            resp.setMessage(RestResponse.Message.RETRIEVED);
            baja = queueBaja;
        } else {
            baja.setDateProcessed(LocalDateTime.now());
            baja.setStatus(this.statusRepository.findByStatus("AWAITING"));
            this.bajaRepository.save(baja);

            Queue queue = new Queue();
            queue.setDateAdded(LocalDateTime.now());
            queue.setProcessType(this.processTypeRepository.findByType("BAJA"));
            queue.setRefId(baja.getId());
            this.queueRepository.save(queue);
            resp.setMessage(RestResponse.Message.CREATED);

        }

        /* Success */
        resp.setData(baja.getId());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
