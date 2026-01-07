package msa;

import com.github.oxo42.stateless4j.StateConfiguration;
import com.github.oxo42.stateless4j.StateMachineConfig;
import msa.AlertStates.ActionlessBaseAlertState;
import msa.AlertStates.BaseAlertState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AlertStateMachineConfig {
    @Autowired
    private List<StateDefinition<State, Trigger, Alert>> stateDefinitions;

    @Autowired
    private AlertTriggers alertTriggers;

    @Bean
    public StateMachineConfig<State, Trigger> alertStateMachineConfiguration() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        alertTriggers.initialize(config);

        stateDefinitions.forEach(stateDefinition -> {
            StateConfiguration<State, Trigger> stateConfiguration =
                    config.configure(stateDefinition.getState());

            if (!(stateDefinition instanceof ActionlessBaseAlertState)) {
                stateConfiguration.onEntryFrom(stateDefinition.getEntryTrigger(), (alert) ->
                        stateDefinition.getAction().doIt(alert));
            }

            if (stateDefinition.getTransitions() != null && !stateDefinition.getTransitions().isEmpty()) {
                stateDefinition.getTransitions().forEach(permission ->
                        stateConfiguration.permitDynamic(permission.trigger, permission.destinationStateSelector));
            }

            if (stateDefinition.ignoreTriggers() != null && !stateDefinition.ignoreTriggers().isEmpty()) {
                stateDefinition.ignoreTriggers().forEach(stateConfiguration::ignore);
            }
        });

        return config;
    }
}
