package Workout.ORM.Repository;

import Workout.ORM.Model.ContractKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContractKeyRepository extends JpaRepository<ContractKey, Long> {
    ContractKey findByCkey(Long s);
}