package msa;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

public class AlertStateMachine {
    private final AlertProcessingActions alertProcessing;
    private final Alert alert;

    private final StateMachine<State, Trigger> machine;

    public AlertStateMachine(AlertProcessingActions alertService, Alert alert) {
        this.alertProcessing = alertService;
        this.alert = alert;

        StateMachineConfig<State, Trigger> config = configure();
        this.machine = new StateMachine<>(State.INITIAL, config);
    }

    public StateMachineConfig<State, Trigger> configure() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        config.configure(State.INITIAL)
                .permit(Trigger.START_AUTO, State.SANITY_CHECK)
                .permit(Trigger.START_MANUAL, State.DISTRIBUTION);

        config.configure(State.SANITY_CHECK)
                .onEntryFrom(
                        Trigger.START_AUTO,
                        () -> {
                            alertProcessing.sanityCheck(alert);
                            machine.fire(Trigger.VALIDATED);
                        }
                )
                .permit(Trigger.VALIDATED, State.ADDITIONAL_CHECK);

        config.configure(State.ADDITIONAL_CHECK)
                .onEntry(() -> {
                    alertProcessing.additionalCheck(alert);
                    machine.fire(Trigger.DISTRIBUTE);
                })
                .permitDynamic(Trigger.DISTRIBUTE, () -> {
                    alertProcessing.addAlertStateMachine(alert.getIncidentId(), this);
                    int time = alertProcessing.calculateInterventionTime(alert.getImpact().getTime(), alert.getAlertTypeId());
                    return (time <= 0) ? State.DISTRIBUTION : State.WAITING;
                });

        config.configure(State.WAITING)
                .onEntry(() -> {
                    alertProcessing.sendAlertToClients(new AlertDistribution(alert));
                    int delaySeconds = alertProcessing.calculateInterventionTime(
                            alert.getImpact().getTime(),
                            alert.getAlertTypeId()
                    );

                    alertProcessing.scheduleWaitExpired(alert, delaySeconds);
                })
                .permit(Trigger.WAIT_EXPIRED, State.DISTRIBUTION)
                .permit(Trigger.INVALID, State.INVALIDATED);

        config.configure(State.DISTRIBUTION)
                .onEntryFrom(Trigger.DISTRIBUTE, () -> {
                    alertProcessing.distribute(new AlertDistribution(alert));
                    machine.fire(Trigger.INVALID);
                })
                .onEntryFrom(Trigger.WAIT_EXPIRED, () -> {
                    alertProcessing.sendCancellationToClients(alert.getIncidentId());
                    alertProcessing.distribute(new AlertDistribution(alert));
                    machine.fire(Trigger.INVALID);
                })
                .permit(Trigger.INVALID, State.INVALIDATED);

        config.configure(State.INVALIDATED)
                .ignore(Trigger.START_AUTO)
                .ignore(Trigger.VALIDATED)
                .ignore(Trigger.WAIT_EXPIRED)
                .ignore(Trigger.DISTRIBUTE)
                .ignore(Trigger.INVALID);

        return config;
    }

    public void fire(Trigger trigger) {
        machine.fire(trigger);
    }

    public State getState() {
        return machine.getState();
    }
}
