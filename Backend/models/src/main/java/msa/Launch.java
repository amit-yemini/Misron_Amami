package msa;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Launch {
    @JsonProperty
    private long timeSent;
    @JsonProperty
    private float x;
    @JsonProperty
    private float y;
}
