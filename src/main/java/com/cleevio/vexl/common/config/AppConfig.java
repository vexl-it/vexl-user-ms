package com.cleevio.vexl.common.config;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.sun.jna.NativeLibrary;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.TimeZone;

@Configuration
@EnableScheduling
@EnableAsync
@EnableCaching
@ComponentScan(basePackages = "com.cleevio")
public class AppConfig {

    public static final String MARKET_CHART = "market_chart";
    public static final String COIN_PRICE = "coin_price";

    static {
        NativeLibrary.addSearchPath(CLibrary.FULL_NAME_WIN, CLibrary.PATH_LINUX);
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(64000);
        return loggingFilter;
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache(MARKET_CHART),
                new ConcurrentMapCache(COIN_PRICE)));
        return cacheManager;
    }

}
