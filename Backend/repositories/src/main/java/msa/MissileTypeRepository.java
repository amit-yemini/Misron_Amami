package msa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissileTypeRepository extends JpaRepository<MissileType, Integer> {
    @EntityGraph(attributePaths = {"relatedAlertTypes"})
    List<MissileType> findAll();
}
