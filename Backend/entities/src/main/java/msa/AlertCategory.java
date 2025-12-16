package msa;

import org.infinispan.protostream.annotations.ProtoEnumValue;

public enum AlertCategory {
    @ProtoEnumValue(number = 1)
    SECURITY,
    @ProtoEnumValue(number = 2)
    ENVIRONMENTAL,
    @ProtoEnumValue(number = 3, name = "CATEGORY_OTHER")
    OTHER
}
