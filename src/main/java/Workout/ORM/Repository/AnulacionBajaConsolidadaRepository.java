package Workout.ORM.Repository;

import Workout.ORM.Model.AnulacionBajaConsolidada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AnulacionBajaConsolidadaRepository extends JpaRepository<AnulacionBajaConsolidada, Long> {
    AnulacionBajaConsolidada findByIdOrderByDateProcessedDesc(long id);
}