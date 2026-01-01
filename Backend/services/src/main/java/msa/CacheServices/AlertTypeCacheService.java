package msa.CacheServices;

import msa.*;
import org.infinispan.Cache;
import org.infinispan.commons.api.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertTypeCacheService {
    @Autowired
    private Cache<Integer, AlertType> alertTypeCache;

    public AlertType getAlertTypeByCategoryAndEvent(AlertCategory category, AlertEvent event) {
        List<AlertType> found;
        Query<AlertType> query = alertTypeCache.query(
                "FROM msa.AlertType " +
                        "WHERE category = :category AND event = :event");

        query.setParameter("category", category);
        query.setParameter("event", event);

        found = query.execute().list();

        if (found.isEmpty()) {
            throw new NotFoundException("msa.Alert Type with category "
                    + category
                    + " and event "
                    + event +
                    " not found");
        }

        return found.getFirst();
    }

    public boolean isAlertTypeConnectedToMissile(int alertTypeId, int missileId) {
        return alertTypeCache.get(alertTypeId).getRelatedMissileTypes()
                .contains(new MissileType(missileId));
    }

    public int getDistributionTime(int alertTypeId) {
        Query<AlertType> query = alertTypeCache.query("FROM msa.AlertType WHERE id = :id");
        query.setParameter("id", alertTypeId);
        AlertType alertType = query.execute().list().getFirst();
        return alertType.getDistributionTime();
    }
}
