package Workout.ORM.Repository;

import Workout.ORM.Model.ProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StatusRepository extends JpaRepository<ProcessStatus, Long> {
    ProcessStatus findByStatus(String s);
}