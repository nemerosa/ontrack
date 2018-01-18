package net.nemerosa.ontrack.service.support;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Deprecated
public class GuavaCacheFactoryBean implements FactoryBean<ConcurrentMapCache> {

    private final ConcurrentMap<Object, Object> store;
    private final ConcurrentMapCache cache;

    /**
     * Definition for the cache
     *
     * @param name                 Name of the cache
     * @param maxSize              Maximum number of elements in the cache
     * @param expirationAccessTime Expiration time in minutes
     */
    public GuavaCacheFactoryBean(String name, int maxSize, int expirationAccessTime) {
        this.store = CacheBuilder.newBuilder()
                .expireAfterAccess(expirationAccessTime, TimeUnit.MINUTES)
                .maximumSize(maxSize)
                .build()
                .asMap();
        this.cache = new ConcurrentMapCache(name, store, true);
    }

    @Override
    public ConcurrentMapCache getObject() throws Exception {
        return cache;
    }

    @Override
    public Class<?> getObjectType() {
        return ConcurrentMapCache.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}