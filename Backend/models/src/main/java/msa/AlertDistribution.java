package msa;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AlertDistribution {
    @JsonProperty("incident")
    @NotNull
    @Positive
    private Integer incidentId;
    @JsonProperty
    @NotNull
    @Positive
    private Integer identifier;
    @JsonProperty("sent")
    @NotNull
    private Long timeSent;
    @JsonProperty("source")
    @NotNull
    @Positive
    private Integer sourceId;
    @JsonProperty("alertType")
    @NotNull
    @Positive
    private Integer alertTypeId;
    @JsonProperty
    @NotNull
    @Positive
    private Integer missileType;
    @JsonProperty
    @NotNull
    @Valid
    private Impact impact;

    public AlertDistribution(Alert alert) {
        this.incidentId = alert.getIncidentId();
        this.identifier = alert.getIdentifier();
        this.timeSent = alert.getTimeSent();
        this.sourceId = alert.getSourceId();
        this.alertTypeId = alert.getAlertTypeId();
        this.missileType = alert.getMissileType();
        this.impact = alert.getImpact();
    }
}
