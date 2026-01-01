package msa;

import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AlertTriggers {
    private TriggerWithParameters2<Alert, State, Trigger> nextTrigger;
    private TriggerWithParameters2<Alert, State, Trigger> startAutoTrigger;
    private TriggerWithParameters2<Alert, State, Trigger> startManualTrigger;
    private TriggerWithParameters2<Alert, State, Trigger> invalidateTrigger;

    public void initialize(StateMachineConfig<State, Trigger> config) {
        this.nextTrigger = config.setTriggerParameters(Trigger.NEXT, Alert.class, State.class);
        this.startAutoTrigger = config.setTriggerParameters(Trigger.START_AUTO, Alert.class, State.class);
        this.startManualTrigger = config.setTriggerParameters(Trigger.START_MANUAL, Alert.class, State.class);
        this.invalidateTrigger = config.setTriggerParameters(Trigger.INVALID, Alert.class, State.class);
    }

}
