package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.common.Caches;
import net.nemerosa.ontrack.service.support.GuavaCacheFactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() throws Exception {
        SimpleCacheManager o = new SimpleCacheManager();
        o.setCaches(
                Arrays.asList(
                        new GuavaCacheFactoryBean(Caches.SETTINGS, 1, 600).getObject(),
                        new GuavaCacheFactoryBean("properties", 10_000, 60 * 6).getObject()
                )
        );
        return o;
    }
}
