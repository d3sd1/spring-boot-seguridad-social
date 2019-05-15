package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class ContractKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long ckey;

    @OneToOne
    private ContractType type;
    @OneToOne
    private ContractTimeType timeType;
    @Column(nullable = true, unique = false)
    private String description = "";

    public ContractKey() {
    }

    public ContractKey(String key) {
        this.ckey = Integer.parseInt(key);
    }

    public long getCkey() {
        return ckey;
    }

    public void setCkey(long ckey) {
        this.ckey = ckey;
    }

    public ContractType getType() {
        return type;
    }

    public void setType(ContractType type) {
        this.type = type;
    }

    public ContractTimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(ContractTimeType timeType) {
        this.timeType = timeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
