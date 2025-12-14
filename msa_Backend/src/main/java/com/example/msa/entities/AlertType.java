package com.example.msa.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.api.annotations.indexing.Keyword;
import org.infinispan.protostream.annotations.ProtoField;

@Entity
@Indexed
@Data
public class AlertType {
    @Id
    private int id;
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

    @ProtoField(number = 1, defaultValue = "0")
    public int getId() {
        return id;
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
}
