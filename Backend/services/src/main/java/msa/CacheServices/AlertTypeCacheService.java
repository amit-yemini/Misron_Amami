package msa.CacheServices;

import msa.*;
import msa.DBEntities.AlertCategory;
import msa.DBEntities.AlertEvent;
import msa.DBEntities.AlertType;
import msa.DBEntities.MissileType;
import org.infinispan.Cache;
import org.infinispan.commons.api.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AlertTypeCacheService {
    @Autowired
    private Cache<Integer, AlertType> alertTypeCache;

    public AlertType getAlertTypeByCategoryAndEvent(AlertCategory category, AlertEvent event, Alert alert) {
        return alertTypeCache.values().stream()
                .filter(alertType -> alertType.getCategory() == category)
                .filter(alertType -> alertType.getEvent() == event)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Alert Type with category "
                                + category
                                + " and event "
                                + event
                                + " not found",
                        alert
                ));
    }

    public boolean isAlertTypeConnectedToMissile(int alertTypeId, int missileId) {
        return alertTypeCache.get(alertTypeId).getRelatedMissileTypes()
                .contains(new MissileType(missileId));
    }

    public int getDistributionTime(int alertTypeId) {
        AlertType alertType = alertTypeCache.values().stream()
                .filter(type -> Objects.equals(type.getId(), alertTypeId))
                .findFirst()
                .orElseThrow();

        return alertType.getDistributionTime();
    }
}
