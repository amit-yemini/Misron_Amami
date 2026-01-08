package msa.CacheServices;

import msa.Alert;
import msa.DBEntities.MissileType;
import msa.NotFoundException;
import org.infinispan.Cache;
import org.infinispan.commons.api.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MissileTypeCacheService {
    @Autowired
    private Cache<Integer, MissileType> missileTypeCache;

    public MissileType getMissileTypeByExternalId(int externalMissileId, Alert alert) {
        Query<MissileType> query = missileTypeCache.query(
                "FROM msa.DBEntities.MissileType " +
                        "WHERE externalId = :externalId");

        query.setParameter("externalId", externalMissileId);
        List<MissileType> found = query.execute().list();

        if (found.isEmpty()) {
            throw new NotFoundException("Missile Type with external id " + externalMissileId + " not found", alert);
        }

        return found.getFirst();
    }
}
