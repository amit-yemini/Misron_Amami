package msa;

import com.github.oxo42.stateless4j.StateMachine;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.spring.embedded.provider.SpringEmbeddedCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class InfinispanConfig {

    @Bean
    public SpringEmbeddedCacheManager cacheManager() throws Exception {
        DefaultCacheManager cacheManager = new DefaultCacheManager("infinispan.xml");

        return new SpringEmbeddedCacheManager(cacheManager);
    }

    @Bean
    public SerializationContextInitializer msaSchema() {
        return new MsaSchemaImpl();
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
    public Cache<Integer, StateMachine<State, Trigger>> alertStateMachineCache(SpringEmbeddedCacheManager cacheManager) {
        return cacheManager.getNativeCacheManager().getCache("alert-state-machine-cache");
    }

    @Bean
    public Cache<String, StateMachine<State, Trigger>> incomingAlertStateMachineCache(SpringEmbeddedCacheManager cacheManager) {
        return cacheManager.getNativeCacheManager().getCache("incoming-alert-state-machine-cache");
    }
}
