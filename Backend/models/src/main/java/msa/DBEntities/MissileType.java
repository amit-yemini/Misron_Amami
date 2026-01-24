package msa.DBEntities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import msa.DBEntities.Events.CacheConfig;
import msa.DBEntities.Events.RelatedCache;
import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@CacheConfig(cacheName = "missile-type-cache")
public class MissileType extends BaseEntity {
    @Column
    private String name;
    @Column
    private int externalId;

    @ManyToMany(fetch = FetchType.LAZY,
            targetEntity = AlertType.class)
    @JoinTable(name = "alert_to_missile",
            inverseJoinColumns = @JoinColumn(name = "alert_type_id",
                    nullable = false),
            joinColumns = @JoinColumn(name = "missile_type_id",
                    nullable = false),
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    @ToString.Exclude
    @JsonIgnoreProperties("relatedMissileTypes")
    @RelatedCache(cacheName = "alert-type-cache", mappedBy = "relatedMissileTypes")
    private Set<AlertType> relatedAlertTypes = new HashSet<>();

    @Override
    @ProtoField(number = 1, defaultValue = "0")
    @EqualsAndHashCode.Include
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
}
