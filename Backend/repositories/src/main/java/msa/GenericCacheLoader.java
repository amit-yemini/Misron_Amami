package msa;

import org.infinispan.Cache;
import org.springframework.data.jpa.repository.JpaRepository;

public class GenericCacheLoader<K, V extends BaseEntity<K>> implements CacheLoader{
    private final JpaRepository<V, K> repository;
    private final Cache<K, V> cache;

    public GenericCacheLoader(JpaRepository<V, K> repository, Cache<K, V> cache) {
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
