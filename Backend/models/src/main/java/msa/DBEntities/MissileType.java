package msa.DBEntities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class MissileType extends BaseEntity {
    @Column
    private String name;
    @Column
    private int externalId;

    @ManyToMany(mappedBy = "relatedMissileTypes", fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonIgnoreProperties("relatedMissileTypes")
    private Set<AlertType> relatedAlertTypes = new HashSet<>();

    @Override
    @ProtoField(number = 1, defaultValue = "0")
    public Integer getId() {
        return super.id;
    }
    @ProtoField(2)
    public String getName() {
        return name;
    }
    @ProtoField(number = 3, defaultValue = "0")
    public int getExternalId() {
        return externalId;
    }

    public MissileType(Integer id) {
        this.id = id;
    }
}
