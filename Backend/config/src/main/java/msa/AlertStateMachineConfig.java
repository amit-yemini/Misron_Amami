package msa;

import com.github.oxo42.stateless4j.StateConfiguration;
import com.github.oxo42.stateless4j.StateMachineConfig;
import msa.CacheServices.AlertStateCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AlertStateMachineConfig {
    @Autowired
    private List<BaseStateDefinition<State, Trigger, Alert>> baseStateDefinitions;
    @Autowired
    private List<StateDefinition<State, Trigger, Alert>> stateDefinitions;

    @Autowired
    private AlertTriggers alertTriggers;

    @Autowired
    private AlertStateCacheService alertStateCacheService;

    @Autowired
    private CircuitBreakerExecutor circuitBreakerExecutor;

    @Bean
    public StateMachineConfig<State, Trigger> alertStateMachineConfiguration() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        alertTriggers.initialize(config);

        baseStateDefinitions.forEach(baseStateDefinition -> {
            StateConfiguration<State, Trigger> stateConfiguration =
                    config.configure(baseStateDefinition.getState());
            configTransitions(baseStateDefinition, stateConfiguration);
            configIgnoreTriggers(baseStateDefinition, stateConfiguration);
        });

        stateDefinitions.forEach(stateDefinition ->
        {
            configAction(stateDefinition, config.configure(stateDefinition.getState()));
        });

        return config;
    }

    private void configTransitions(BaseStateDefinition<State, Trigger, Alert> baseStateDefinition, StateConfiguration<State, Trigger> stateConfiguration) {
        if (baseStateDefinition.getTransitions() != null && !baseStateDefinition.getTransitions().isEmpty()) {
            baseStateDefinition.getTransitions().forEach(transition ->
                    stateConfiguration.permitDynamic(alertTriggers.get(transition.trigger), transition.destinationStateSelector));
        }
    }

    private void configIgnoreTriggers(BaseStateDefinition<State, Trigger, Alert> baseStateDefinition, StateConfiguration<State, Trigger> stateConfiguration) {
        if (baseStateDefinition.ignoreTriggers() != null && !baseStateDefinition.ignoreTriggers().isEmpty()) {
            baseStateDefinition.ignoreTriggers().forEach(stateConfiguration::ignore);
        }
    }

    private void configAction(StateDefinition<State, Trigger, Alert> stateDefinition, StateConfiguration<State, Trigger> stateConfiguration) {
        stateConfiguration.onEntryFrom(
                stateDefinition.getEntryTrigger(),
                (alert) -> {
                    alertStateCacheService.updateState(alert, stateDefinition.getState());
                    circuitBreakerExecutor.execute(
                            () -> stateDefinition.getAction().doIt(alert)
                    );
                });
    }
}
