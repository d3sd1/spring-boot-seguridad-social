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
public class ConsultaNaf extends Consulta {
    @Column(nullable = false, unique = false)
    private String ipf;

    @Column(nullable = false, unique = false)
    private String ipt;

    @Column(nullable = false, unique = false)
    private String ap1;

    @Column(nullable = false, unique = false)
    private String ap2;

    public String getIpf() {
        return ipf;
    }

    public void setIpf(String ipf) {
        this.ipf = ipf;
    }

    public String getIpt() {
        return ipt;
    }

    public void setIpt(String ipt) {
        this.ipt = ipt;
    }

    public String getAp1() {
        return ap1;
    }

    public void setAp1(String ap1) {
        this.ap1 = ap1;
    }

    public String getAp2() {
        return ap2;
    }

    public void setAp2(String ap2) {
        this.ap2 = ap2;
    }
}
