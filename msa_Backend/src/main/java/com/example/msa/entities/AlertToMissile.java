package com.example.msa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Entity
@IdClass(CompositeKey.class)
@Data
@EqualsAndHashCode
@NoArgsConstructor
public class AlertToMissile {
    @Id
    private int alertTypeId;
    @Id
    private int missileTypeId;

    @ProtoFactory
    public AlertToMissile(int alertTypeId, int missileTypeId) {
        this.alertTypeId = alertTypeId;
        this.missileTypeId = missileTypeId;
    }

    @ProtoField(number = 1, defaultValue = "0")
    public int getAlertTypeId() {
        return alertTypeId;
    }

    @ProtoField(number = 2, defaultValue = "1")
    public int getMissileTypeId() {
        return missileTypeId;
    }
}
