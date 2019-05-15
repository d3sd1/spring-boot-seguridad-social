package Workout.ORM.Repository;

import Workout.ORM.Model.Alta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AltaRepository extends JpaRepository<Alta, Long> {
    Alta findByIdOrderByDateProcessedDesc(long id);
}