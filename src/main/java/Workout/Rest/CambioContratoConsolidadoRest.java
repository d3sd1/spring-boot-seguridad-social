package Workout.Rest;

import Workout.ORM.Model.CambioContratoConsolidado;
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
@RequestMapping("/cambio/contrato/consolidado")
public class CambioContratoConsolidadoRest {

    @Autowired
    private CambioContratoConsolidadoRepository cambioContratoConsolidadoRepository;

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

    @RequestMapping(value = "/{opId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getAlta(@PathVariable long opId) {
        RestResponse resp = new RestResponse();

        CambioContratoConsolidado alta = this.cambioContratoConsolidadoRepository.findByIdOrderByDateProcessedDesc(opId);

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

        CambioContratoConsolidado cambioContratoConsolidado = this.cambioContratoConsolidadoRepository.findByIdOrderByDateProcessedDesc(altaId);
        boolean success = false;
        if (cambioContratoConsolidado == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        if (cambioContratoConsolidado.getStatus().getStatus().equals("AWAITING") || cambioContratoConsolidado.getStatus().getStatus().equals("STOPPED")) {
            cambioContratoConsolidado.setStatus(this.statusRepository.findByStatus("REMOVED"));
            cambioContratoConsolidado.setProcessTime(0);
            this.cambioContratoConsolidadoRepository.delete(cambioContratoConsolidado);
            success = true;
        }
        resp.setData(success);
        resp.setMessage("DELETE_STATUS");
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> postAlta(@RequestBody CambioContratoConsolidado cambioContratoConsolidado) {
        RestResponse resp = new RestResponse();

        /* Previous checks and preloads. No se revisa el COE ya que es para tiempo parcial. */
        cambioContratoConsolidado.setTco(this.contractKeyRepository.findByCkey(cambioContratoConsolidado.getTco().getCkey()));
        if (cambioContratoConsolidado.getTco() == null) {
            resp.setMessage(RestResponse.Message.INVALID_OBJECT);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        cambioContratoConsolidado.setCoe(this.contractCoeRepository.findByCoefficient(cambioContratoConsolidado.getCoe().getCoefficient()));

        /*
         * Validar el tipo de empresa.
         */
        cambioContratoConsolidado.setCca(this.contractAccountRepository.findByName(cambioContratoConsolidado.getCca().getName()));
        if (cambioContratoConsolidado.getCca() == null) {
            resp.setMessage(RestResponse.Message.CONTRACT_ACCOUNT_NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        cambioContratoConsolidado.setDateInit(LocalDateTime.now());
        cambioContratoConsolidado.setProcessTime(0);

        if (cambioContratoConsolidado.getIpt().equals(6)) {
            cambioContratoConsolidado.setIpf("0" + cambioContratoConsolidado.getIpf());
        }

        /*
         * El alta no puede sobrepasar 60 días posteriores a la actual.
         * Además, la fecha no puede ser anterior a la actual.
         */
        if (LocalDate.now().plus(2, ChronoUnit.DAYS).isAfter(cambioContratoConsolidado.getFrc())) {
            resp.setMessage(RestResponse.Message.DATE_PASSED);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        /*
         * Si el contrato es de tipo parcial, se requiere su coeficiente, y que éste sea válido.
         */
        if (cambioContratoConsolidado.getTco().getTimeType().getTimeType().equals("TIEMPO_PARCIAL") && cambioContratoConsolidado.getCoe() == null) {
            resp.setMessage(RestResponse.Message.CONTRACT_PARTIAL_COE);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        CambioContratoConsolidado queueCambioContratoConsolidado = this.queueService.isCambioContratoConsolidadoOnQueue(cambioContratoConsolidado);
        if (queueCambioContratoConsolidado != null) {
            resp.setMessage(RestResponse.Message.RETRIEVED);
            cambioContratoConsolidado = queueCambioContratoConsolidado;
        } else {
            cambioContratoConsolidado.setDateProcessed(LocalDateTime.now());
            cambioContratoConsolidado.setStatus(this.statusRepository.findByStatus("AWAITING"));
            this.cambioContratoConsolidadoRepository.save(cambioContratoConsolidado);

            Queue queue = new Queue();
            queue.setDateAdded(LocalDateTime.now());
            queue.setProcessType(this.processTypeRepository.findByType("CAMBIO_CONTRATO_CONSOLIDADO"));
            queue.setRefId(cambioContratoConsolidado.getId());
            this.queueRepository.save(queue);
            resp.setMessage(RestResponse.Message.CREATED);

        }

        /* Success */
        resp.setData(cambioContratoConsolidado.getId());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
