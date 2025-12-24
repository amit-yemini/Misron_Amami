package msa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AlertProcessingService implements AlertProcessingActions{
    @Autowired
    private LaunchCountryCacheService launchCountryCacheService;
    @Autowired
    private AlertTypeCacheService alertTypeCacheService;
    @Autowired
    private MissileTypeCacheService missileTypeCacheService;
    @Autowired
    private AlertStateCacheService alertStateCacheService;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(2);

    public void scheduleWaitExpired(Alert alert, int delaySeconds) {
        scheduler.schedule(
                () -> onWaitExpired(alert), delaySeconds, TimeUnit.SECONDS
        );
    }

    private void onWaitExpired(Alert alert) {
        AlertStateMachine alertStateMachine =
                alertStateCacheService.getAlertStateMachine(alert.getIncidentId());
        alertStateMachine.fire(Trigger.WAIT_EXPIRED);
    }

    public void distribute(AlertDistribution alertDistribution) {
        System.out.println("distributing alert");
    }

    public void sendToClients(AlertDistribution alertDistribution) {
        System.out.println("sending to clients");
    }

    public int calculateInterventionTime(long impactTime, int alertTypeId) {
        int distributionTime = alertTypeCacheService.getDistributionTime(alertTypeId);

        return Math.toIntExact(impactTime - Instant.now().getEpochSecond() - distributionTime);
    }

    public void sanityCheck(Alert alert) throws NotFoundException, InvalidSenderException {
        launchCountryCheck(alert.getSourceId());
        alert.setAlertTypeId(getAlertType(alert.getCategory(), alert.getEvent()));
        checkAlertToMissileMatch(alert.getAlertTypeId(), alert.getMissileType());
        checkSender(alert.getSender());
    }

    public void additionalCheck(Alert alert) throws AlertDiscreditedException {
        checkSendTime(alert.getTimeSent());
        checkImpactTime(alert.getImpact().getTime());
        checkAlertRelevance(alert.getIncidentId());
    }

    private void launchCountryCheck(int externalId) throws NotFoundException {
        LaunchCountry launchCountry = launchCountryCacheService
                .getLaunchCountryByExternalId(externalId);

        log.info("launch country id: {}", launchCountry.getId());
    }

    private int getAlertType(AlertCategory category, AlertEvent event) throws NotFoundException {
        AlertType found = alertTypeCacheService.getAlertTypeByCategoryAndEvent(category, event);

        log.info("alert type id: {}", found.getId());

        return found.getId();
    }

    private void checkAlertToMissileMatch(int alertTypeId, int externalMissileId) throws NotFoundException {
        int missileId = missileTypeCacheService
                .getMissileTypeByExternalId(externalMissileId).getId();
        log.info("missile id: {}", missileId);

        boolean doesCombinationExist =
                alertTypeCacheService.isAlertTypeConnectedToMissile(alertTypeId, missileId);

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
        alertStateCacheService.checkAlertRelevance(incidentId);
    }

    public void addAlertStateMachine(int incidentId, AlertStateMachine stateMachine) {
        alertStateCacheService.addAlertStateMachine(incidentId, stateMachine);
    }
}
