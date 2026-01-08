package msa;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AlertDistribution {
    @JsonProperty("incident")
    @NotNull
    @Positive
    private Integer incidentId;
    @JsonProperty
    @NotNull
    @Positive
    private Integer identifier;
    @JsonProperty
    private String sender = "MisronAmami";
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
}
