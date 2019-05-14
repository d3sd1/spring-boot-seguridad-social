package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class ContractCoefficient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long coefficient;

    private String description = "";

    public long getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(long coefficient) {
        this.coefficient = coefficient;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
