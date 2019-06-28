package Workout.Rest;

import Workout.ORM.Model.Alta;
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
@RequestMapping("/alta")
@CrossOrigin
public class AltaRest {

    @Autowired
    private ContractKeyRepository contractKeyRepository;

    @Autowired
    private ContractCoefficientRepository contractCoeRepository;

    @Autowired
    private ContractAccountRepository contractAccountRepository;

    @Autowired
    private AltaRepository altaRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ProcessTypeRepository processTypeRepository;

    @Autowired
    private QueueService queueService;

    @RequestMapping(value = "/{altaId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getAlta(@PathVariable long altaId) {
        RestResponse resp = new RestResponse();

        Alta alta = this.altaRepository.findByIdOrderByDateProcessedDesc(altaId);

        resp.setData("");
        if (alta == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        resp.setMessage(alta.getStatus().getStatus());
        resp.setData(alta.getErrMsg());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "/{altaId}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delAlta(@PathVariable long altaId) {
        RestResponse resp = new RestResponse();

        Alta alta = this.altaRepository.findByIdOrderByDateProcessedDesc(altaId);
        boolean success = false;
        if (alta == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        if (alta.getStatus().getStatus().equals("AWAITING") || alta.getStatus().getStatus().equals("STOPPED")) {
            alta.setStatus(this.statusRepository.findByStatus("REMOVED"));
            alta.setProcessTime(0);
            this.altaRepository.delete(alta);
            success = true;
        }
        resp.setData(success);
        resp.setMessage("DELETE_STATUS");
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> postAlta(@RequestBody Alta alta) {
        RestResponse resp = new RestResponse();

        /* Previous checks and preloads. No se revisa el COE ya que es para tiempo parcial. */
        alta.setTco(this.contractKeyRepository.findByCkey(alta.getTco().getCkey()));
        if (alta.getTco() == null) {
            resp.setMessage(RestResponse.Message.INVALID_OBJECT);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        alta.setCoe(this.contractCoeRepository.findByCoefficient(alta.getCoe().getCoefficient()));

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

        if (Integer.parseInt(alta.getIpt()) == 6) {
            alta.setIpf("0" + alta.getIpf());
        }
        /*
         * Validar situación. Si no introdujo nada, el valor por defecto es 01.
         */
        if (alta.getSit() == null) {
            alta.setSit("01");
        }

        /*
         * El alta no puede sobrepasar 60 días posteriores a la actual.
         * Además, la fecha no puede ser anterior a la actual.
         */
        if (LocalDate.now().plus(60, ChronoUnit.DAYS).isBefore(alta.getFra())) {
            resp.setMessage(RestResponse.Message.DATE_EXPIRE_INVALID);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        if (alta.getFra().isBefore(LocalDate.now())) {
            resp.setMessage(RestResponse.Message.DATE_PASSED);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        /*
         * Si el contrato es de tipo parcial, se requiere su coeficiente, y que éste sea válido.
         */
        if (alta.getTco().getTimeType().getTimeType().equals("TIEMPO_PARCIAL") && alta.getCoe() == null) {
            resp.setMessage(RestResponse.Message.CONTRACT_PARTIAL_COE);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        /* Verificar naf */
        if(alta.getNaf() == null || alta.getNaf().equals("") || alta.getNaf().length() < 10) {
            resp.setMessage(RestResponse.Message.INVALID_NAF);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        Alta queueAlta = this.queueService.isAltaOnQueue(alta);
        if (queueAlta != null) {
            resp.setMessage(RestResponse.Message.RETRIEVED);
            alta = queueAlta;
        } else {
            alta.setDateProcessed(LocalDateTime.now());
            alta.setStatus(this.statusRepository.findByStatus("AWAITING"));
            this.altaRepository.save(alta);

            Queue queue = new Queue();
            queue.setDateAdded(LocalDateTime.now());
            queue.setProcessType(this.processTypeRepository.findByType("ALTA"));
            queue.setRefId(alta.getId());
            this.queueRepository.save(queue);
            resp.setMessage(RestResponse.Message.CREATED);

        }

        /* Success */
        resp.setData(alta.getId());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
