package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class AnulacionAltaConsolidada extends Operation {
    @OneToOne
    private ContractAccount cca;

    @Column(nullable = false, unique = true)
    private long naf;

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
}
