package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class Baja extends Operation {
    @OneToOne
    private ContractAccount cca;

    @Column(nullable = false, unique = false)
    private String naf;

    @Column(nullable = false, unique = false)
    private String ipf;

    @Column(nullable = false, unique = false)
    private String ipt;

    @Column(nullable = false, unique = false)
    private LocalDate frb;

    @Column(nullable = true, unique = false)
    private LocalDate ffv = null;

    @Column(nullable = true, unique = false)
    private int gco;

    @Column(nullable = true, unique = false)
    private String sit;

    @OneToOne
    private ContractKey tco;

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

    public int getGco() {
        return gco;
    }

    public void setGco(int gco) {
        this.gco = gco;
    }

    public String getSit() {
        return sit;
    }

    public void setSit(String sit) {
        this.sit = sit;
    }

    public ContractKey getTco() {
        return tco;
    }

    public void setTco(ContractKey tco) {
        this.tco = tco;
    }

    public ContractCoefficient getCoe() {
        return coe;
    }

    public void setCoe(ContractCoefficient coe) {
        this.coe = coe;
    }

    public LocalDate getFrb() {
        return frb;
    }

    public void setFrb(LocalDate frb) {
        this.frb = frb;
    }

    public LocalDate getFfv() {
        return ffv;
    }

    public void setFfv(LocalDate ffv) {
        this.ffv = ffv;
    }
}
