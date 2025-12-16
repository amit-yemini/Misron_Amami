package msa;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.ProtoSchema;

@ProtoSchema(
        schemaPackageName = "msa",
        includeClasses = {
                LaunchCountry.class,
                AlertType.class,
                AlertCategory.class,
                AlertEvent.class,
                MissileType.class,
                AlertToMissile.class
        }
)
public interface MsaSchema extends GeneratedSchema {
}
