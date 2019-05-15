package Workout.ORM.Repository;

import Workout.ORM.Model.AnulacionBajaPrevia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AnulacionBajaPreviaRepository extends JpaRepository<AnulacionBajaPrevia, Long> {
    AnulacionBajaPrevia findByIdOrderByDateProcessedDesc(long id);
}