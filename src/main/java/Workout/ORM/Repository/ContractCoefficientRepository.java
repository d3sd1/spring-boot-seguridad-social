package Workout.ORM.Repository;

import Workout.ORM.Model.ContractCoefficient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContractCoefficientRepository extends JpaRepository<ContractCoefficient, Long> {
    ContractCoefficient findByCoefficient(Long s);
}