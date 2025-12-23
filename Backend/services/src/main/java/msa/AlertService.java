package msa;

import lombok.extern.slf4j.Slf4j;
import org.infinispan.Cache;
import org.infinispan.commons.api.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class AlertService {
    @Autowired
    private Cache<Integer, LaunchCountry> launchCountryCache;
    @Autowired
    private Cache<Integer, AlertType> alertTypeCache;
    @Autowired
    private Cache<Integer, MissileType> missileTypeCache;
    @Autowired
    private Cache<Integer, Alert> alertCache;

    public void processAlert(Alert alert) throws NotFoundException, InvalidSenderException {
        sanityCheck(alert);
        additionalCheck(alert);
        alertCache.put(alert.getIncidentId(), alert);

        final int INTERVENTION_TIME = calculateInterventionTime(alert.getImpact().getTime(),
                alert.getAlertTypeId());

        if (INTERVENTION_TIME <= 0) {
            distribute(new AlertDistribution(alert));
        } else {
            sendToClient(new AlertDistribution(alert));
        }
    }

    private void distribute(AlertDistribution alertDistribution) {}

    private void sendToClient(AlertDistribution alertDistribution) {}

    private int calculateInterventionTime(long impactTime, int alertTypeId) {
        Query<AlertType> query = alertTypeCache.query("FROM msa.AlertType WHERE id = :id");
        query.setParameter("id", alertTypeId);
        AlertType alertType = query.execute().list().getFirst();
        int distributionTime = alertType.getDistributionTime();

        return Math.toIntExact(impactTime - Instant.now().getEpochSecond() - distributionTime);
    }

    private void sanityCheck(Alert alert) throws NotFoundException, InvalidSenderException {
        launchCountryCheck(alert.getSourceId());
        alert.setAlertTypeId(getAlertType(alert.getCategory(), alert.getEvent()));
        checkAlertToMissileMatch(alert.getAlertTypeId(), alert.getMissileType());
        checkSender(alert.getSender());
    }

    private void additionalCheck(Alert alert) throws AlertDiscreditedException {
        checkSendTime(alert.getTimeSent());
        checkImpactTime(alert.getImpact().getTime());
        checkAlertRelevance(alert.getIncidentId());
    }

    private void launchCountryCheck(int externalId) throws NotFoundException {
        Query<LaunchCountry> query = launchCountryCache.query(
                "FROM msa.LaunchCountry " +
                "WHERE externalId = :externalId");

        query.setParameter("externalId", externalId);
        List<LaunchCountry> found = query.execute().list();

        if (found.isEmpty()) {
            throw new NotFoundException("msa.Launch Country with external id " + externalId + " not found");
        }

        log.info("launch country id: {}", found.getFirst().getId());
    }

    private int getAlertType(AlertCategory category, AlertEvent event) throws NotFoundException {
        Query<AlertType> query = alertTypeCache.query(
                "FROM msa.AlertType " +
                        "WHERE category = :category AND event = :event");

        query.setParameter("category", category);
        query.setParameter("event", event);

        List<AlertType> found = query.execute().list();

        if (found.isEmpty()) {
            throw new NotFoundException("msa.Alert Type with category "
                    + category
                    + " and event "
                    + event +
                    " not found");
        }

        log.info("alert type id: {}", found.getFirst().getId());

        return found.getFirst().getId();
    }

    private void checkAlertToMissileMatch(int alertId, int externalMissileId) throws NotFoundException {
        Query<MissileType> query = missileTypeCache.query(
                "FROM msa.MissileType " +
                        "WHERE externalId = :externalId");

        query.setParameter("externalId", externalMissileId);
        List<MissileType> found = query.execute().list();

        if (found.isEmpty()) {
            throw new NotFoundException("Missile Type with external id " + externalMissileId + " not found");
        }

        int missileId = found.get(0).getId();
        log.info("missile id: {}", missileId);

        boolean doesCombinationExist = alertTypeCache.get(alertId).getRelatedMissileTypes()
                .contains(new MissileType(missileId));

        if (!doesCombinationExist) {
            throw new NotFoundException("alert and missile combination doesn't exists");
        }
    }

    private void checkSender(String sender) throws InvalidSenderException {
        if (!sender.equals("BurningKite")) {
            throw new InvalidSenderException(sender);
        }
    }

    private void checkSendTime(long sendTime) {
        if (!Instant.ofEpochSecond(sendTime).isBefore(Instant.now())) {
            throw new DateTimeException("Send time must be in the past");
        }
    }

    private void checkImpactTime(long impactTime) {
        if (!Instant.ofEpochSecond(impactTime).isAfter(Instant.now())) {
            throw new DateTimeException("msa.Impact time must be in the future");
        }
    }

    private void checkAlertRelevance(int incidentId) throws AlertDiscreditedException {
        if (alertCache.containsKey(incidentId)) {
            Alert previousAlert = alertCache.get(incidentId);
            if (previousAlert.isManual() || previousAlert.isCancelled()) {
                throw new AlertDiscreditedException(incidentId);
            }
        }
    }
}
