package Workout.ORM.Model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Transactional
@Table()
@EntityListeners(AuditingEntityListener.class)
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(nullable = true, unique = false)
    private LocalDateTime dateAdded;

    @Column(nullable = false, unique = false)
    private int refId;

    @OneToOne()
    private ProcessType processType;


}
