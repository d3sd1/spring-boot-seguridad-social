package Workout.ORM.Repository;

import Workout.ORM.Model.ConsultaIpf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConsultaIpfRepository extends JpaRepository<ConsultaIpf, Long> {
    ConsultaIpf findByIdOrderByDateProcessedDesc(long id);
}