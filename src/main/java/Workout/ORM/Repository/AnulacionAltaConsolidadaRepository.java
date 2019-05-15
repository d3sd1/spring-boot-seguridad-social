package Workout.ORM.Repository;

import Workout.ORM.Model.AnulacionAltaConsolidada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AnulacionAltaConsolidadaRepository extends JpaRepository<AnulacionAltaConsolidada, Long> {
    AnulacionAltaConsolidada findByIdOrderByDateProcessedDesc(long id);
}