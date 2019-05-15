package Workout.ORM.Repository;

import Workout.ORM.Model.CambioContratoPrevio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CambioContratoPrevioRepository extends JpaRepository<CambioContratoPrevio, Long> {
    CambioContratoPrevio findByIdOrderByDateProcessedDesc(long id);
}