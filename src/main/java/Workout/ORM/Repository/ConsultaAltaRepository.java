package Workout.ORM.Repository;

import Workout.ORM.Model.ConsultaAlta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConsultaAltaRepository extends JpaRepository<ConsultaAlta, Long> {
    ConsultaAlta findByIdOrderByDateProcessedDesc(long id);
}