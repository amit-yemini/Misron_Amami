package msa.AlertStateMachine;

import lombok.AllArgsConstructor;
import lombok.Data;
import msa.Alert;

@Data
@AllArgsConstructor
public class AlertContext {
    private int identifier;
    private State state;

    public AlertContext(Alert alert, State state) {
        this.state = state;
        this.identifier = alert.getIdentifier();
    }
}
