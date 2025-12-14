package com.example.msa.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Impact {
    @JsonProperty
    private long time;
    @JsonProperty
    private float x;
    @JsonProperty
    private float y;
    @JsonProperty
    private float ellipseA;
    @JsonProperty
    private float ellipseB;
}
