package msa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertToMissileRepository extends JpaRepository<AlertToMissile, CompositeKey> {
}
