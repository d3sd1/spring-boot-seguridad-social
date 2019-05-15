package Workout.ORM.Repository;

import Workout.ORM.Model.CambioContratoConsolidado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CambioContratoConsolidadoRepository extends JpaRepository<CambioContratoConsolidado, Long> {
    CambioContratoConsolidado findByIdOrderByDateProcessedDesc(long id);
}