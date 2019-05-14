package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class ConsultaAlta extends Consulta {
    @OneToOne
    private ContractAccount cca;

    @Column(nullable = false, unique = true)
    private long naf;

    @Column(nullable = false, unique = false)
    private LocalDateTime frc;

    public ContractAccount getCca() {
        return cca;
    }

    public void setCca(ContractAccount cca) {
        this.cca = cca;
    }

    public long getNaf() {
        return naf;
    }

    public void setNaf(long naf) {
        this.naf = naf;
    }

    public LocalDateTime getFrc() {
        return frc;
    }

    public void setFrc(LocalDateTime frc) {
        this.frc = frc;
    }
}