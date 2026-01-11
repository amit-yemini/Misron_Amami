package msa;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlertContext {
    private Alert alert;
    private State state;
}
