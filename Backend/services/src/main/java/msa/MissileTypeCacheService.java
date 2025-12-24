package msa;

import org.infinispan.Cache;
import org.infinispan.commons.api.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MissileTypeCacheService {
    @Autowired
    private Cache<Integer, MissileType> missileTypeCache;

    public MissileType getMissileTypeByExternalId(int externalMissileId) {
        Query<MissileType> query = missileTypeCache.query(
                "FROM msa.MissileType " +
                        "WHERE externalId = :externalId");

        query.setParameter("externalId", externalMissileId);
        List<MissileType> found = query.execute().list();

        if (found.isEmpty()) {
            throw new NotFoundException("Missile Type with external id " + externalMissileId + " not found");
        }

        return found.getFirst();
    }
}
