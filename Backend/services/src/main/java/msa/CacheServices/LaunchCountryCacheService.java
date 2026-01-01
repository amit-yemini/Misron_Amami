package msa.CacheServices;

import msa.LaunchCountry;
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

    public LaunchCountry getLaunchCountryByExternalId(int externalId) {
        Query<LaunchCountry> query = launchCountryCache.query(
                "FROM msa.LaunchCountry " +
                        "WHERE externalId = :externalId");

        query.setParameter("externalId", externalId);
        List<LaunchCountry> found = query.execute().list();

        if (found.isEmpty()) {
            throw new NotFoundException("msa.Launch Country with external id " + externalId + " not found");
        }

        return found.getFirst();
    }
}
