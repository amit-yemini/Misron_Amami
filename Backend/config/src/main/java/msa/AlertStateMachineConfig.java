package msa;

import com.github.oxo42.stateless4j.StateConfiguration;
import com.github.oxo42.stateless4j.StateMachineConfig;
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

            // config on entry action
            if (stateDefinition.getEntryTrigger() != null && stateDefinition.getAction() != null) {
                stateConfiguration.onEntryFrom(stateDefinition.getEntryTrigger(), (alert, fromState) ->
                        stateDefinition.getAction().doIt(alert, fromState));
            }

            // config permits
            if (stateDefinition.getPermissions() != null && !stateDefinition.getPermissions().isEmpty()) {
                stateDefinition.getPermissions().forEach(permission ->
                        stateConfiguration.permitDynamic(permission.trigger, permission.destinationStateSelector));
            }

            // config ignores
            if (stateDefinition.ignoreTriggers() != null && !stateDefinition.ignoreTriggers().isEmpty()) {
                stateDefinition.ignoreTriggers().forEach(stateConfiguration::ignore);
            }
        });

        return config;
    }
}
