package Workout.ORM.Repository;

import Workout.ORM.Model.ContractAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContractAccountRepository extends JpaRepository<ContractAccount, Long> {
    ContractAccount findByName(String s);
}