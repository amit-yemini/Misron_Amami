package msa;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import msa.DBEntities.AlertCategory;
import msa.DBEntities.AlertEvent;

@Data
public class Alert {
    @JsonProperty("incident")
    @NotNull
    @Positive
    private Integer incidentId;
    @JsonProperty
    @NotNull
    @Positive
    private Integer identifier;
    @JsonProperty
    @NotNull
    private String sender;
    @JsonProperty("sent")
    @NotNull
    private Long timeSent;
    @JsonProperty("source")
    @NotNull
    @Positive
    private Integer sourceId;
    @JsonProperty("category")
    @NotNull
    @Valid
    private AlertCategory category;
    @JsonProperty("event")
    @NotNull
    @Valid
    private AlertEvent event;
    @JsonProperty
    @NotNull
    @Positive
    private Integer missileType;
    @JsonProperty
    @NotNull
    @Valid
    private Launch launch;
    @JsonProperty
    @NotNull
    @Valid
    private Impact impact;

    private int alertTypeId;
}
