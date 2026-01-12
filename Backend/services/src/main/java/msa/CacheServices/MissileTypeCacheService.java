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
        return missileTypeCache.values().stream()
                .filter(missileType -> missileType.getExternalId() == externalMissileId)
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException(
                                "Missile Type with external id "
                                        + externalMissileId
                                        + " not found",
                                alert
                        )
                );
    }
}
