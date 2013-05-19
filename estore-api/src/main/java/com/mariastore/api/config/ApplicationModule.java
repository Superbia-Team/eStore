package com.mariastore.api.config;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class ApplicationModule
{
	@Bean
	public CacheManager cacheManager() throws Exception {
		SimpleCacheManager simpleCache = new SimpleCacheManager();
		simpleCache.setCaches(Arrays.asList(new ConcurrentMapCache("tags")));
		return simpleCache;
	}
}
