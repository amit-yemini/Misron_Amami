package com.example.msa.config;

import com.example.msa.entities.*;
import com.example.msa.models.Alert;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.protostream.annotations.ProtoSchema;
import org.infinispan.spring.embedded.provider.SpringEmbeddedCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfinispanConfig {

    @Bean
    public SpringEmbeddedCacheManager cacheManager() throws Exception {
        DefaultCacheManager cacheManager = new DefaultCacheManager("infinispan.xml");

        return new SpringEmbeddedCacheManager(cacheManager);
    }

    @Bean
    public Cache<Integer, LaunchCountry> launchCountryCache(SpringEmbeddedCacheManager cacheManager) {
        return cacheManager.getNativeCacheManager().getCache("launch-country-cache");
    }

    @Bean
    public Cache<Integer, AlertType> alertTypeCache(SpringEmbeddedCacheManager cacheManager) {
        return cacheManager.getNativeCacheManager().getCache("alert-type-cache");
    }

    @Bean
    public Cache<Integer, MissileType> missileTypeCache(SpringEmbeddedCacheManager cacheManager) {
        return cacheManager.getNativeCacheManager().getCache("missile-type-cache");
    }

    @Bean
    public Cache<AlertToMissile, Object> alertToMissileCache(SpringEmbeddedCacheManager cacheManager) {
        return cacheManager.getNativeCacheManager().getCache("alert-to-missile-cache");
    }

    @Bean
    public Cache<Integer, Alert> alertsCache(SpringEmbeddedCacheManager cacheManager) {
        return cacheManager.getNativeCacheManager().getCache("alerts-cache");
    }

    @ProtoSchema(includeClasses = {
            LaunchCountry.class,
            AlertType.class,
            AlertCategory.class,
            AlertEvent.class,
            MissileType.class,
            AlertToMissile.class
    })
    public interface MsaSchema extends GeneratedSchema {
    }
}
