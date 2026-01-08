package msa;

import jakarta.persistence.criteria.CriteriaBuilder;
import msa.DBEntities.BaseEntity;
import org.infinispan.Cache;
import org.springframework.data.jpa.repository.JpaRepository;

public class GenericCacheLoader<V extends BaseEntity> implements CacheLoader{
    private final JpaRepository<V, Integer> repository;
    private final Cache<Integer, V> cache;

    public GenericCacheLoader(JpaRepository<V, Integer> repository, Cache<Integer, V> cache) {
        this.repository = repository;
        this.cache = cache;
    }

    @Override
    public void load() {
        repository.findAll().forEach(
                entity -> cache.put(entity.getId(), entity)
        );
    }
}
