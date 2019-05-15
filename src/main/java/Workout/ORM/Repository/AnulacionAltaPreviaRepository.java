package Workout.ORM.Repository;

import Workout.ORM.Model.AnulacionAltaPrevia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AnulacionAltaPreviaRepository extends JpaRepository<AnulacionAltaPrevia, Long> {
    AnulacionAltaPrevia findByIdOrderByDateProcessedDesc(long id);
}