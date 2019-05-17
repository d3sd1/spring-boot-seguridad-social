package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class AnulacionBajaPrevia extends Operation {
    @OneToOne
    private ContractAccount cca;

    @Column(nullable = false, unique = true)
    private String naf;

    @Column(nullable = false, unique = false)
    private LocalDate frb;

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

    public LocalDate getFrb() {
        return frb;
    }

    public void setFrb(LocalDate frb) {
        this.frb = frb;
    }
}
