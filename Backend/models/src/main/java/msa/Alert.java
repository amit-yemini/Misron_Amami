package msa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Alert {
    @JsonProperty
    private int incidentId;
    @JsonProperty
    private int identifier;
    @JsonProperty
    private String sender;
    @JsonProperty
    private long timeSent;
    @JsonProperty
    private int sourceId;
    @JsonProperty
    private AlertCategory category;
    @JsonProperty
    private AlertEvent event;
    @JsonProperty
    private int missileType;
    @JsonProperty
    private Launch launch;
    @JsonProperty
    private Impact impact;
    @JsonProperty
    private float azimuth;

    private int alertId;
    private boolean isManual = false;
    private boolean isCancelled = false;
}
