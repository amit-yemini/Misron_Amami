package msa.DBEntities.Events;

import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import msa.DBEntities.BaseEntity;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

@Component
@Slf4j
public class CacheCleanupListener implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @PreRemove
    public void preRemove(Object entity) throws IllegalAccessException, NoSuchFieldException {
        if (!(entity instanceof BaseEntity deletedEntity)) return;

        Integer id = deletedEntity.getId();
        Class<?> clazz = entity.getClass();

        EmbeddedCacheManager cacheManager = context.getBean(EmbeddedCacheManager.class);

        if (clazz.isAnnotationPresent(CacheConfig.class)) {
            String cacheName = clazz.getAnnotation(CacheConfig.class).cacheName();
            removeEntry(cacheManager, cacheName, id);
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(RelatedCache.class)) {
                processRelatedSet(cacheManager, deletedEntity, field);
            }
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (!(entity instanceof BaseEntity updatedEntity)) return;

        Class<?> clazz = updatedEntity.getClass();
        EmbeddedCacheManager cacheManager = context.getBean(EmbeddedCacheManager.class);

        // 1. Update the entity itself in its primary cache
        if (clazz.isAnnotationPresent(CacheConfig.class)) {
            String primaryCache = clazz.getAnnotation(CacheConfig.class).cacheName();
            cacheManager.getCache(primaryCache).put(updatedEntity.getId(), updatedEntity);
        }

        // 2. Sync related collections
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(RelatedCache.class)) {
                syncRelatedCache(cacheManager, updatedEntity, field);
            }
        }
    }

    private void syncRelatedCache(EmbeddedCacheManager cacheManager, BaseEntity updatedEntity, Field field) {
        RelatedCache anno = field.getAnnotation(RelatedCache.class);
        String cacheName = anno.cacheName();
        String mappedBy = anno.mappedBy();
        Cache<Integer, Object> cache = cacheManager.getCache(cacheName);

        try {
            field.setAccessible(true);
            Object value = field.get(updatedEntity);

            if (value instanceof Set<?> parentSet) {
                for (Object parentObj : parentSet) {
                    if (parentObj instanceof BaseEntity parentBase) {
                        updateOrAddInCachedParent(cache, parentBase.getId(), mappedBy, updatedEntity);
                    }
                }
            }
        } catch (Exception e) {
            // Log reflection/access error
        }
    }

    private void updateOrAddInCachedParent(Cache<Integer, Object> cache, Integer parentId, String fieldName, BaseEntity updatedEntity) {
        cache.computeIfPresent(parentId, (key, cachedParent) -> {
            try {
                Field collectionField = cachedParent.getClass().getDeclaredField(fieldName);
                collectionField.setAccessible(true);
                Object collectionObj = collectionField.get(cachedParent);

                if (collectionObj instanceof Collection collection) {
                    // Remove old version if it exists (based on ID)
                    collection.removeIf(item ->
                            item instanceof BaseEntity be && be.getId().equals(updatedEntity.getId())
                    );
                    // Add the fresh updated version
                    collection.add(updatedEntity);
                }
            } catch (Exception e) {
                return cachedParent;
            }
            return cachedParent;
        });
    }

    private void removeEntry(EmbeddedCacheManager cacheManager, String cacheName, Object key) {
        if (cacheManager.getCacheNames().contains(cacheName)) {
            cacheManager.getCache(cacheName).remove(key);
            System.out.println("Evicted key " + key + " from cache: " + cacheName);
        }
    }

    private void processRelatedSet(EmbeddedCacheManager cacheManager, BaseEntity deletedEntity, Field field) {
        RelatedCache annotation = field.getAnnotation(RelatedCache.class);
        String cacheName = annotation.cacheName();
        String mappedBy = annotation.mappedBy();
        Cache<Integer, Object> cache = cacheManager.getCache(cacheName);

        try {
            field.setAccessible(true);
            Object value = field.get(deletedEntity);

            if (value instanceof Set<?> parentSet) {
                for (Object parentObj : parentSet) {
                    if (parentObj instanceof BaseEntity parentBase) {
                        updateCachedParent(cache, parentBase.getId(), mappedBy, deletedEntity.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void updateCachedParent(Cache<Integer, Object> cache, Integer parentId, String fieldName, Integer idToRemove) {
        cache.computeIfPresent(parentId, (key, cachedValue) -> {
            try {
                Field collectionField = cachedValue.getClass().getDeclaredField(fieldName);
                collectionField.setAccessible(true);
                Object collectionObj = collectionField.get(cachedValue);

                if (collectionObj instanceof Collection<?> collection) {
                    collection.removeIf(item ->
                            item instanceof BaseEntity baseEntity && baseEntity.getId().equals(idToRemove)
                    );
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return cachedValue;
            }
            return cachedValue;
        });
    }
}