package msa;

import msa.DBEntities.AlertType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertTypeRepository extends JpaRepository<AlertType, Integer> {
    @EntityGraph(attributePaths = {"relatedMissileTypes"})
    List<AlertType> findAll();
}
