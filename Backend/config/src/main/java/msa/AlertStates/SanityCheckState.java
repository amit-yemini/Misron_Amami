package msa.AlertStates;

import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import lombok.extern.slf4j.Slf4j;
import msa.*;
import msa.CacheServices.AlertTypeCacheService;
import msa.CacheServices.IncomingAlertStateMachineCacheService;
import msa.CacheServices.LaunchCountryCacheService;
import msa.CacheServices.MissileTypeCacheService;
import msa.DBEntities.AlertCategory;
import msa.DBEntities.AlertEvent;
import msa.DBEntities.AlertType;
import msa.DBEntities.LaunchCountry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SanityCheckState extends BaseAlertState {
    @Autowired
    private LaunchCountryCacheService launchCountryCacheService;
    @Autowired
    private AlertTypeCacheService alertTypeCacheService;
    @Autowired
    private MissileTypeCacheService missileTypeCacheService;
    @Autowired
    private IncomingAlertStateMachineCacheService incomingAlertStateMachineCacheService;
    @Autowired
    private AlertTriggers alertTriggers;

    @Override
    public State getState() {
        return State.SANITY_CHECK;
    }

    @Override
    public void execute(Alert alert) {
        log.info("starting sanity check for alert: incident: {}, identifier: {}",
                alert.getIncidentId(), alert.getIdentifier());
        launchCountryCheck(alert.getSourceId());
        alert.setAlertTypeId(getAlertType(alert.getCategory(), alert.getEvent()));
        checkAlertToMissileMatch(alert.getAlertTypeId(), alert.getMissileType());
        checkSender(alert.getSender());
        incomingAlertStateMachineCacheService.fire(alertTriggers.get(Trigger.NEXT), alert, getState());
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getTransitions() {
        return List.of(
                new Transition<>(
                        alertTriggers.get(Trigger.NEXT),
                        State.ADDITIONAL_CHECK
                )
        );
    }

    @Override
    public TriggerWithParameters1<Alert, Trigger> getEntryTrigger() {
        return alertTriggers.get(Trigger.START_AUTO);
    }

    @Override
    public List<Trigger> ignoreTriggers() {
        return List.of();
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
}
