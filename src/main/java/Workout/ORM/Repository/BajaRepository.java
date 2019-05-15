package Workout.ORM.Repository;

import Workout.ORM.Model.Baja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BajaRepository extends JpaRepository<Baja, Long> {
    Baja findByIdOrderByDateProcessedDesc(long id);
}