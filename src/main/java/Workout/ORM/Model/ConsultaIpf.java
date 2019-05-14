package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class ConsultaIpf extends Consulta {
    @Column(nullable = false, unique = true)
    private long naf;

    public long getNaf() {
        return naf;
    }

    public void setNaf(long naf) {
        this.naf = naf;
    }
}
