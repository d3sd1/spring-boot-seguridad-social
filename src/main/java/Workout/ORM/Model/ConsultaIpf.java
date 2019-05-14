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
    private String naf;

    public String getNaf() {
        return naf;
    }

    public void setNaf(String naf) {
        this.naf = naf;
    }
}
