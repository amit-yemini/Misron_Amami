package msa;

import msa.DBEntities.AlertType;
import msa.DBEntities.LaunchCountry;
import msa.DBEntities.MissileType;
import org.infinispan.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheLoaderConfig {
    @Bean
    public CacheLoader launchCountryLoader(
            LaunchCountryRepository repo,
            Cache<Integer, LaunchCountry> cache) {

        return new GenericCacheLoader<>(
                repo,
                cache
        );
    }

    @Bean
    public CacheLoader alertTypeLoader(
            AlertTypeRepository repo,
            Cache<Integer, AlertType> cache) {

        return new GenericCacheLoader<>(
                repo,
                cache
        );
    }

    @Bean
    public CacheLoader missileTypeLoader(
            MissileTypeRepository repo,
            Cache<Integer, MissileType> cache) {

        return new GenericCacheLoader<>(
                repo,
                cache
        );
    }
}
