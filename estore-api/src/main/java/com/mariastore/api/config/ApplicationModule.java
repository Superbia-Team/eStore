package com.mariastore.api.config;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableCaching
@ComponentScan(basePackages = { ApplicationModule.BASE_PACKEAGE })
@ImportResource( { "classpath:/api-security.xml" } )
public class ApplicationModule
{	
	public static final String BASE_PACKEAGE = "com.mariastore";
	public static final String WEB_PACKAGE = 	BASE_PACKEAGE + ".api.web";
	public static final String CORE_PACKAGE = 	BASE_PACKEAGE + ".core.domain";
	public static final String API_PACKAGE = 	BASE_PACKEAGE + ".api.domain";
	
	public ApplicationModule() { super(); }
	
	@Bean
	public CacheManager cacheManager() throws Exception {
		SimpleCacheManager simpleCache = new SimpleCacheManager();
		simpleCache.setCaches(Arrays.asList(new ConcurrentMapCache("tags")));
		return simpleCache;
	}
}
