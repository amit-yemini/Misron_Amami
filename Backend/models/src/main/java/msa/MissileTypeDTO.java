package msa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import msa.DBEntities.AlertType;

import java.util.Set;
@Data
public class MissileTypeDTO {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private int externalId;
    @JsonProperty
    private Set<AlertType> relatedAlertTypes;
}
