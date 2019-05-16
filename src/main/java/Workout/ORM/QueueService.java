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

    public AnulacionAltaPrevia isAnulacionAltaPreviaOnQueue(AnulacionAltaPrevia alta) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("ANULACION_ALTA_PREVIA"));

        for (Queue proccess : queue) {
            AnulacionAltaPrevia qAlta = this.anulacionAltaPreviaRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qAlta == null || qAlta.getNaf() == null || alta == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qAlta.getNaf().equals(alta.getNaf())) {
                return qAlta;
            }
        }
        return null;
    }

    public AnulacionAltaConsolidada isAnulacionAltaConsolidadaOnQueue(AnulacionAltaConsolidada alta) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("ANULACION_ALTA_CONSOLIDADA"));

        for (Queue proccess : queue) {
            AnulacionAltaConsolidada qAlta = this.anulacionAltaConsolidadaRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qAlta == null || qAlta.getNaf() == null || alta == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qAlta.getNaf().equals(alta.getNaf())) {
                return qAlta;
            }
        }
        return null;
    }

    public AnulacionBajaPrevia isAnulacionBajaPreviaOnQueue(AnulacionBajaPrevia baja) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("ANULACION_BAJA_PREVIA"));

        for (Queue proccess : queue) {
            AnulacionBajaPrevia qAlta = this.anulacionBajaPreviaRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qAlta == null || qAlta.getNaf() == null || baja == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qAlta.getNaf().equals(baja.getNaf())) {
                return qAlta;
            }
        }
        return null;
    }

    public AnulacionBajaConsolidada isAnulacionBajaConsolidadaOnQueue(AnulacionBajaConsolidada baja) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("ANULACION_BAJA_CONSOLIDADA"));

        for (Queue proccess : queue) {
            AnulacionBajaConsolidada qBaja = this.anulacionBajaConsolidadaRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qBaja == null || qBaja.getNaf() == null || qBaja.getIpf() == null || baja == null || baja.getIpf() == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qBaja.getNaf().equals(baja.getNaf()) && qBaja.getIpf().equals(baja.getIpf())) {
                return qBaja;
            }
        }
        return null;
    }

    public ConsultaNaf isConsultaNafOnQueue(ConsultaNaf consultaNaf) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("CONSULTA_NAF"));

        for (Queue proccess : queue) {
            ConsultaNaf qConsultaNaf = this.consultaNafRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qConsultaNaf == null || qConsultaNaf.getIpf() == null || consultaNaf == null || consultaNaf.getIpf() == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qConsultaNaf.getIpf().equals(consultaNaf.getIpf())) {
                return qConsultaNaf;
            }
        }
        return null;
    }

    public ConsultaIpf isConsultaIpfOnQueue(ConsultaIpf consultaIpf) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("CONSULTA_IPF"));

        for (Queue proccess : queue) {
            ConsultaIpf qConsultaIpf = this.consultaIpfRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qConsultaIpf == null || qConsultaIpf.getNaf() == null || consultaIpf == null || consultaIpf.getNaf() == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qConsultaIpf.getNaf().equals(consultaIpf.getNaf())) {
                return qConsultaIpf;
            }
        }
        return null;
    }

    public ConsultaTa isConsultaTaOnQueue(ConsultaTa consultaTa) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("CONSULTA_TA"));

        for (Queue proccess : queue) {
            ConsultaTa qConsultaTa = this.consultaTaRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qConsultaTa == null || qConsultaTa.getNaf() == null || consultaTa == null || consultaTa.getNaf() == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qConsultaTa.getNaf().equals(consultaTa.getNaf())) {
                return qConsultaTa;
            }
        }
        return null;
    }

    public ConsultaAlta isConsultaAltaOnQueue(ConsultaAlta consultaAlta) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("CONSULTA_ALTA"));

        for (Queue proccess : queue) {
            ConsultaAlta qConsultaAlta = this.consultaAltaRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qConsultaAlta == null || qConsultaAlta.getNaf() == null || consultaAlta == null || consultaAlta.getNaf() == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qConsultaAlta.getNaf().equals(consultaAlta.getNaf())) {
                return qConsultaAlta;
            }
        }
        return null;
    }

    public ConsultaAltasCcc isConsultaCccOnQueue(ConsultaAltasCcc consultaAltasCcc) {

        List<Queue> queue = this.queueRepository.findAllByProcessType(this.processTypeRepository.findByType("CONSULTA_ALTAS_CCC"));

        for (Queue proccess : queue) {
            ConsultaAltasCcc qConsultAltasCcc = this.consultaAltasCccRepository.findByIdOrderByDateProcessedDesc(proccess.getRefId());

            // Delete if the key is missing.
            if (qConsultAltasCcc == null || qConsultAltasCcc.getCca() == null || consultaAltasCcc == null || consultaAltasCcc.getCca() == null) {
                this.queueRepository.delete(proccess);
                continue;
            }
            if (qConsultAltasCcc.getCca().equals(consultaAltasCcc.getCca())) {
                return qConsultAltasCcc;
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
