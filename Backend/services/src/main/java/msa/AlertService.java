package msa;

import org.infinispan.Cache;
import org.infinispan.commons.api.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.List;

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

    public void newAlert(Alert alert) throws NotFoundException, InvalidSenderException {
        sanityCheck(alert);
        additionalCheck(alert);
        alertCache.put(alert.getIncidentId(), alert);

    }

    private void sanityCheck(Alert alert) throws NotFoundException, InvalidSenderException {
        launchCountryCheck(alert.getSourceId());
        alert.setAlertId(getAlertType(alert.getCategory(), alert.getEvent()));
        checkAlertToMissileMatch(alert.getAlertId(), alert.getMissileType());
        checkSender(alert.getSender());
    }

    private void additionalCheck(Alert alert) throws AlertDiscreditedException {
        checkSendTime(alert.getTimeSent());
        checkImpactTime(alert.getImpact().getTime());
        checkAlertDiscredit(alert.getIncidentId());
    }

    private void launchCountryCheck(int externalId) throws NotFoundException {
//        Query<LaunchCountry> query = launchCountryCache.query(
//                "FROM msa.LaunchCountry " +
//                "WHERE externalId = :externalId");
//
//        query.setParameter("externalId", externalId);
//        List<LaunchCountry> found = query.execute().list();
//
//        if (found.isEmpty()) {
//            throw new NotFoundException("msa.Launch Country with external id " + externalId + " not found");
//        }
//
//        System.out.println("launch country id: " + found.get(0).getId());


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

        System.out.println("alert type id: " + found.get(0).getId());
        return found.get(0).getId();
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
        System.out.println(missileId);

        boolean doesCombinationExists = alertTypeCache.get(alertId).getRelatedMissileTypes()
                .stream()
                .map(MissileType::getId).toList().contains(missileId);

        if (!doesCombinationExists) {
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

    private void checkAlertDiscredit(int incidentId) throws AlertDiscreditedException {
        if (alertCache.containsKey(incidentId)) {
            Alert previousAlert = alertCache.get(incidentId);
            if (previousAlert.isManual() || previousAlert.isCancelled()) {
                throw new AlertDiscreditedException(incidentId);
            }
        }
    }
}
