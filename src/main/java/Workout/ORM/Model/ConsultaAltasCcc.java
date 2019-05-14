package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class ConsultaAltasCcc extends Consulta {
    @OneToOne
    private ContractAccount cca;

    public ContractAccount getCca() {
        return cca;
    }

    public void setCca(ContractAccount cca) {
        this.cca = cca;
    }
}
