package Workout.ORM.Repository;

import Workout.ORM.Model.ConsultaNaf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConsultaNafRepository extends JpaRepository<ConsultaNaf, Long> {
    ConsultaNaf findByIdOrderByDateProcessedDesc(long id);
}