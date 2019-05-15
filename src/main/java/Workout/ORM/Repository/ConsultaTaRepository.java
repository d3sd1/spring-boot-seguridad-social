package Workout.ORM.Repository;

import Workout.ORM.Model.ConsultaTa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConsultaTaRepository extends JpaRepository<ConsultaTa, Long> {
    ConsultaTa findByIdOrderByDateProcessedDesc(long id);
}