package com.example.msa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.api.annotations.indexing.Indexed;

@Entity
@Indexed
@Data
public class LaunchCountry {
    @Id
    @GeneratedValue
    private int id;
    @Column
    private String name;
    @Column
    private int externalId;

    @ProtoField(number = 1, defaultValue = "0")
    public int getId() {
        return id;
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
