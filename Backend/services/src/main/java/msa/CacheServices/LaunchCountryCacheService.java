package msa.CacheServices;

import msa.Alert;
import msa.DBEntities.LaunchCountry;
import msa.NotFoundException;
import org.infinispan.Cache;
import org.infinispan.commons.api.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaunchCountryCacheService {
    @Autowired
    private Cache<Integer, LaunchCountry> launchCountryCache;

    public LaunchCountry getLaunchCountryByExternalId(int externalId, Alert alert) {
        Query<LaunchCountry> query = launchCountryCache.query(
                "FROM msa.DBEntities.LaunchCountry " +
                        "WHERE externalId = :externalId");

        query.setParameter("externalId", externalId);
        List<LaunchCountry> found = query.execute().list();

        if (found.isEmpty()) {
            throw new NotFoundException("msa.Launch Country with external id " + externalId + " not found", alert);
        }

        return found.getFirst();
    }
}
