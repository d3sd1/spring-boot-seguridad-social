package Workout.Rest;

import Workout.ORM.Model.ConsultaIpf;
import Workout.ORM.Model.Queue;
import Workout.ORM.QueueService;
import Workout.ORM.Repository.ConsultaIpfRepository;
import Workout.ORM.Repository.ProcessTypeRepository;
import Workout.ORM.Repository.QueueRepository;
import Workout.ORM.Repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/consulta/ipf")
@CrossOrigin
public class ConsultaIpfRest {
    @Autowired
    private ConsultaIpfRepository consultaIpfRepository;
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

        ConsultaIpf consultaIpf = this.consultaIpfRepository.findByIdOrderByDateProcessedDesc(consultaId);

        resp.setData("");
        if (consultaIpf == null) {
            resp.setMessage(RestResponse.Message.NOT_FOUND);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        String data = "";
        if (consultaIpf.getStatus().getStatus().equals("COMPLETED")) {
            data = consultaIpf.getData();
        } else if (consultaIpf.getStatus().getStatus().equals("ERROR")) {
            data = consultaIpf.getErrMsg();
        }


        resp.setMessage(consultaIpf.getStatus().getStatus());
        resp.setData(data);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> postOp(@RequestBody ConsultaIpf consultaIpf) {
        RestResponse resp = new RestResponse();
        /*
         * Comprobar que no exista una solicitud similar y esté pendiente.
         * Si no hay ninguna, se crea una nueva y se agrega a la cola para el bot.
         * Si existe una previa, se devuelve la ID de la previa, excepto:
         * Si existe y esta en estado de error o completada, que se genera una nueva.
         */
        ConsultaIpf queueConsultaIpf = this.queueService.isConsultaIpfOnQueue(consultaIpf);
        if (queueConsultaIpf != null) {
            resp.setMessage(RestResponse.Message.RETRIEVED);
            consultaIpf = queueConsultaIpf;
        } else {
            consultaIpf.setDateProcessed(LocalDateTime.now());
            consultaIpf.setStatus(this.statusRepository.findByStatus("AWAITING"));
            consultaIpf.setDateInit(LocalDateTime.now());
            this.consultaIpfRepository.save(consultaIpf);

            Queue queue = new Queue();
            queue.setDateAdded(LocalDateTime.now());
            queue.setProcessType(this.processTypeRepository.findByType("CONSULTA_IPF"));
            queue.setRefId(consultaIpf.getId());
            this.queueRepository.save(queue);
            resp.setMessage(RestResponse.Message.CREATED);

        }

        /* Success */
        resp.setData(consultaIpf.getId());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}