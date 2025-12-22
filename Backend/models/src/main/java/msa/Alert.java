package msa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Alert {
    @JsonProperty("incident")
    private int incidentId;
    @JsonProperty
    private int identifier;
    @JsonProperty
    private String sender;
    @JsonProperty("sent")
    private long timeSent;
    @JsonProperty("source")
    private int sourceId;
    @JsonProperty("category")
    private AlertCategory category;
    @JsonProperty("event")
    private AlertEvent event;
    @JsonProperty
    private int missileType;
    @JsonProperty
    private Launch launch;
    @JsonProperty
    private Impact impact;

    private int alertId;
    private boolean isManual = false;
    private boolean isCancelled = false;
}
