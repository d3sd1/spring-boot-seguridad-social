package Workout.ORM.Repository;

import Workout.ORM.Model.ProcessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProcessTypeRepository extends JpaRepository<ProcessType, Long> {
    ProcessType findByType(String s);
}