package Workout.Logger;

import Workout.ORM.Model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByLevel(LogLevel level);
    List<Log> findAllByOrderByObjectIdDesc();

    List<Log> findAll();
}

