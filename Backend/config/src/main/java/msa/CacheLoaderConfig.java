package msa;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import msa.DBEntities.BaseEntity;
import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;

@Configuration
@Slf4j
public class CacheLoaderConfig {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ConfigurableBeanFactory beanFactory;

    @PostConstruct
    public void registerCacheLoaders() {
        Map<String, Object> repositories = applicationContext
                .getBeansWithAnnotation(CacheableRepository.class);

        repositories.forEach((repositoryBeanName, repositoryBean) -> {
            CacheableRepository annotation = AnnotationUtils.findAnnotation(
                    repositoryBean.getClass(),
                    CacheableRepository.class
            );

            if (annotation != null) {
                String loaderBeanName = repositoryBeanName.replace("Repository", "Loader");

                JpaRepository repository = applicationContext.getBean(repositoryBeanName, JpaRepository.class);
                Cache cache = applicationContext.getBean(annotation.cacheName(), Cache.class);

                beanFactory.registerSingleton(loaderBeanName, createCacheLoader(repository, cache));

                log.info("Registered CacheLoader: {}", loaderBeanName);
            }
        });
    }

    private <V extends BaseEntity> GenericCacheLoader<V> createCacheLoader(
            JpaRepository<V, Integer> repository,
            Cache<Integer, V> cache) {
        return new GenericCacheLoader<>(repository, cache);
    }
}
