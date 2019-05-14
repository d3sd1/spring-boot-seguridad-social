package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class CambioContratoConsolidado extends Operation {
    @OneToOne
    private ContractAccount cca;

    @Column(nullable = false, unique = true)
    private String naf;

    @Column(nullable = false, unique = false)
    private String ipf;

    @Column(nullable = false, unique = false)
    private String ipt;

    @OneToOne
    private ContractKey tco;
    @Column(nullable = false, unique = false)
    private LocalDateTime frc;

    @OneToOne
    private ContractCoefficient coe;

    public ContractAccount getCca() {
        return cca;
    }

    public void setCca(ContractAccount cca) {
        this.cca = cca;
    }

    public String getNaf() {
        return naf;
    }

    public void setNaf(String naf) {
        this.naf = naf;
    }

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

    public ContractKey getTco() {
        return tco;
    }

    public void setTco(ContractKey tco) {
        this.tco = tco;
    }

    public LocalDateTime getFrc() {
        return frc;
    }

    public void setFrc(LocalDateTime frc) {
        this.frc = frc;
    }

    public ContractCoefficient getCoe() {
        return coe;
    }

    public void setCoe(ContractCoefficient coe) {
        this.coe = coe;
    }
}
