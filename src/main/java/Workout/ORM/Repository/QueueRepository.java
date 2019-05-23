package Workout.ORM.Repository;

import Workout.ORM.Model.ProcessType;
import Workout.ORM.Model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    List<Queue> findAllByProcessType(ProcessType processType);
    List<Queue> findAllByOrderByIdDesc();

    Queue findByRefIdAndProcessType(long refId, ProcessType processType);
}