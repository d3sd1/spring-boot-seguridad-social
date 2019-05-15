package Workout.ORM;

import Workout.Logger.LogService;
import Workout.ORM.Model.*;
import Workout.ORM.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Service("QueueService")
@Transactional
public class QueueService {
    @Autowired
    private ProcessStatusRepository processStatusRepository;

    @Autowired
    private ProcessTypeRepository processTypeRepository;

    @Autowired
    private AltaRepository altaRepository;

    @Autowired
    private BajaRepository bajaRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private AnulacionAltaConsolidadaRepository anulacionAltaConsolidadaRepository;

    @Autowired
    private AnulacionBajaConsolidadaRepository anulacionBajaConsolidadaRepository;

    @Autowired
    private AnulacionBajaPreviaRepository anulacionBajaPreviaRepository;

    @Autowired
    private AnulacionAltaPreviaRepository anulacionAltaPreviaRepository;

    @Autowired
    private CambioContratoConsolidadoRepository cambioContratoConsolidadoRepository;

    @Autowired
    private CambioContratoPrevioRepository cambioContratoPrevioRepository;

    @Autowired
    private ConsultaAltaRepository consultaAltaRepository;

    @Autowired
    private ConsultaAltasCccRepository consultaAltasCccRepository;

    @Autowired
    private ConsultaIpfRepository consultaIpfRepository;

    @Autowired
    private ConsultaNafRepository consultaNafRepository;

    @Autowired
    private ConsultaTaRepository consultaTaRepository;

    @Autowired
    private LogService logger;

    @PersistenceContext
    private EntityManager em;

    public Alta isAltaOnQueue(Alta alta) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("ALTA"));

        for (Queue proccess : queue) {
            Alta qAlta = this.altaRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qAlta == null || qAlta.getNaf() == null || alta == null || alta.getIpf() == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qAlta.getNaf().equals(alta.getNaf()) && qAlta.getIpf().equals(alta.getIpf())) {
                return qAlta;
            }
        }
        return null;
    }

    public Baja isBajaOnQueue(Baja baja) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("BAJA"));

        for (Queue proccess : queue) {
            Baja qBaja = this.bajaRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qBaja == null || qBaja.getNaf() == null || baja == null || baja.getIpf() == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qBaja.getNaf().equals(baja.getNaf()) && qBaja.getIpf().equals(baja.getIpf())) {
                return qBaja;
            }
        }
        return null;
    }

    public Operation getOpFromQueue(Queue q) {
        String opType = q.getProcessType().getType().toUpperCase();
        Operation op = null;
        switch (opType) {
            case "ALTA":
                op = this.altaRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "ANULACIONALTACONSOLIDADA":
                op = this.anulacionAltaConsolidadaRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "ANULACIONALTAPREVIA":
                op = this.anulacionAltaPreviaRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "ANULACIONBAJACONSOLIDADA":
                op = this.anulacionBajaConsolidadaRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "ANULACIONBAJAPREVIA":
                op = this.anulacionBajaPreviaRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "BAJA":
                op = this.bajaRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "CAMBIOCONTRATOCONSOLIDADO":
                op = this.cambioContratoConsolidadoRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "CAMBIOCONTRATOPREVIO":
                op = null;
                this.logger.error("Cambio contrato previo not yet implemented.");
                break;
            case "CONSULTAALTA":
                op = this.consultaAltaRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "CONSULTAALTASCCC":
                op = this.consultaAltasCccRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "CONSULTAIPF":
                op = this.consultaIpfRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "CONSULTANAF":
                op = this.consultaNafRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;
            case "CONSULTATA":
                op = this.consultaTaRepository.findByIdOrderByDateProcessedDesc(q.getRefId());
                break;

            default:
                this.logger.error("Undefined operation type: " + opType);
        }
        return op;
    }

    public void saveOp(Operation o) {
        if (o instanceof Alta) {
            this.altaRepository.save((Alta) o);
        } else if (o instanceof AnulacionAltaConsolidada) {
            this.anulacionAltaConsolidadaRepository.save((AnulacionAltaConsolidada) o);
        } else if (o instanceof AnulacionAltaPrevia) {
            this.anulacionAltaPreviaRepository.save((AnulacionAltaPrevia) o);
        } else if (o instanceof AnulacionBajaConsolidada) {
            this.anulacionBajaConsolidadaRepository.save((AnulacionBajaConsolidada) o);
        } else if (o instanceof AnulacionBajaPrevia) {
            this.anulacionBajaPreviaRepository.save((AnulacionBajaPrevia) o);
        } else if (o instanceof Baja) {
            this.bajaRepository.save((Baja) o);
        } else if (o instanceof CambioContratoConsolidado) {
            this.cambioContratoConsolidadoRepository.save((CambioContratoConsolidado) o);
        } else if (o instanceof CambioContratoPrevio) {
            this.cambioContratoPrevioRepository.save((CambioContratoPrevio) o);
        } else if (o instanceof ConsultaAlta) {
            this.consultaAltaRepository.save((ConsultaAlta) o);
        } else if (o instanceof ConsultaAltasCcc) {
            this.consultaAltasCccRepository.save((ConsultaAltasCcc) o);
        } else if (o instanceof ConsultaIpf) {
            this.consultaIpfRepository.save((ConsultaIpf) o);
        } else if (o instanceof ConsultaNaf) {
            this.consultaNafRepository.save((ConsultaNaf) o);
        } else if (o instanceof ConsultaTa) {
            this.consultaTaRepository.save((ConsultaTa) o);
        } else {
            this.logger.error("Undefined operation type on saveOp: " + o.getClass().getSimpleName());
        }
    }


}
