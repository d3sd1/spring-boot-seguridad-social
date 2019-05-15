package Workout.ORM.Repository;

import Workout.ORM.Model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface OperationRepository extends JpaRepository<Operation, Long> {
    Operation findByStatus(String s);
}