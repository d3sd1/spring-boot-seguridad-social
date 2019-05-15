package Workout.ORM.Repository;

import Workout.ORM.Model.ConsultaAltasCcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConsultaAltasCccRepository extends JpaRepository<ConsultaAltasCcc, Long> {
    ConsultaAltasCcc findByIdOrderByDateProcessedDesc(long id);
}