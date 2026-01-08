package msa.DBEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.protostream.annotations.ProtoField;

@Entity
@Indexed
@Data
public class LaunchCountry extends BaseEntity {
    @Column
    private String name;
    @Column
    private int externalId;

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
}
