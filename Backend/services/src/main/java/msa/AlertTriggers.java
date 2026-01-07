package msa;

import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@Data
public class AlertTriggers {
    private final Map<Trigger, TriggerWithParameters1<Alert, Trigger>> triggers =
            new EnumMap<>(Trigger.class);

    public void initialize(StateMachineConfig<State, Trigger> config) {
        for (Trigger trigger : Trigger.values()) {
            triggers.put(trigger, config.setTriggerParameters(trigger, Alert.class));
        }
    }

    public TriggerWithParameters1<Alert, Trigger> get(Trigger trigger) {
        return triggers.get(trigger);
    }
}
