package msa.DBEntities;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import msa.DBEntities.Events.CacheCleanupListener;

@MappedSuperclass
@Data
@EntityListeners(CacheCleanupListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    protected Integer id;
}
