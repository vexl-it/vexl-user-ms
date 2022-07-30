package com.cleevio.vexl.common.service.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvictCacheTask {

    private final CacheManager cacheManager;

    @Scheduled(fixedDelay = 70_000)
    public void evictAllCaches() {
        cacheManager.getCacheNames().forEach(c -> cacheManager.getCache(c).clear());
    }
}