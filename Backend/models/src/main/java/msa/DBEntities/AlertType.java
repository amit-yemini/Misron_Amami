package msa.DBEntities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.api.annotations.indexing.Keyword;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed
@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class AlertType extends BaseEntity {
    @Column
    private String name;
    @Column
    @Enumerated(EnumType.STRING)
    private AlertCategory category;
    @Column
    @Enumerated(EnumType.STRING)
    private AlertEvent event;
    @Column
    private int distributionTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "alert_to_missile",
            joinColumns = @JoinColumn(name = "alert_type_id"),
            inverseJoinColumns = @JoinColumn(name = "missile_type_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties("relatedAlertTypes")
    private Set<MissileType> relatedMissileTypes = new HashSet<>();

    @Override
    @ProtoField(number = 1, defaultValue = "0")
    public Integer getId() {
        return super.id;
    }
    @ProtoField(2)
    public String getName() {
        return name;
    }
    @ProtoField(3)
    @Keyword
    public AlertCategory getCategory() {
        return category;
    }
    @ProtoField(4)
    @Keyword
    public AlertEvent getEvent() {
        return event;
    }
    @ProtoField(number = 5, defaultValue = "0")
    public int getDistributionTime() {
        return distributionTime;
    }
    @ProtoField(6)
    public Set<MissileType> getRelatedMissileTypes() {
        return relatedMissileTypes;
    }
}
