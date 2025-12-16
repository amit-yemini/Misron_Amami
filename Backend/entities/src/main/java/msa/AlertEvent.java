package msa;

import org.infinispan.protostream.annotations.ProtoEnumValue;

public enum AlertEvent {
    @ProtoEnumValue(number = 1)
    MISSILE,
    @ProtoEnumValue(number = 2)
    TEST,
    @ProtoEnumValue(number = 3, name = "EVENT_OTHER")
    OTHER
}
