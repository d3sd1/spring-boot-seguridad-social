package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class CambioContratoPrevio extends Operation {
    // TO IMPLEMENT WHEN NEEDED!
}
