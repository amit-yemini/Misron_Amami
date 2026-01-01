package msa.AlertStates;

import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import lombok.extern.slf4j.Slf4j;
import msa.*;
import msa.CacheServices.AlertTypeCacheService;
import msa.CacheServices.IncomingAlertStateMachineCacheService;
import msa.CacheServices.LaunchCountryCacheService;
import msa.CacheServices.MissileTypeCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SanityCheckState implements StateDefinition<State, Trigger, Alert> {
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
    public Action2<Alert, State> getAction() {
        return (alert, state) -> {
            sanityCheck(alert);
            incomingAlertStateMachineCacheService.fire(alertTriggers.getNextTrigger(), alert, state);
        };
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getPermissions() {
        return List.of(
                new Transition<>(
                        alertTriggers.getNextTrigger(),
                        (alert, state) -> State.ADDITIONAL_CHECK
                )
        );
    }

    @Override
    public TriggerWithParameters2<Alert, State, Trigger> getEntryTrigger() {
        return alertTriggers.getStartAutoTrigger();
    }

    @Override
    public List<Trigger> ignoreTriggers() {
        return List.of();
    }

    public void sanityCheck(Alert alert) throws NotFoundException, InvalidSenderException {
        log.info("starting sanity check for alert: incident: {}, identifier: {}",
                alert.getIncidentId(), alert.getIdentifier());
        launchCountryCheck(alert.getSourceId());
        alert.setAlertTypeId(getAlertType(alert.getCategory(), alert.getEvent()));
        checkAlertToMissileMatch(alert.getAlertTypeId(), alert.getMissileType());
        checkSender(alert.getSender());
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
