package msa;

import msa.DBEntities.LaunchCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheableRepository(cacheName = "launchCountryCache")
public interface LaunchCountryRepository extends JpaRepository<LaunchCountry, Integer> {

}
