package com.laba3;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.laba3.exceptions.AsyncExceptionHandler;
import com.laba3.exceptions.RestTemplateResponseErrorHandler;
import com.laba3.utils.CurrencyNamingConverter;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
@EnableCaching
public class AppConfiguration implements AsyncConfigurer {

    @Value("${properties.async.corePoolSize}")
    private int corePoolSize;

    @Value("${properties.async.maxPoolSize}")
    private int maxPoolSize;

    @Value("${properties.async.queueCapacity}")
    private int queueCapacity;

    @Value("${properties.connectTimeout}")
    private int connectTimeout;

    @Value("${properties.readTimeout}")
    private int readTimeout;

    @Bean
    public RestTemplate getRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
            @Override
            public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
                configurer.favorParameter(true).
                        parameterName("format").
                        ignoreAcceptHeader(false).
                        defaultContentType(MediaType.APPLICATION_JSON).
                        mediaType("xml", MediaType.APPLICATION_XML).
                        mediaType("json", MediaType.APPLICATION_JSON);
            }
        };
    }

    @Bean
    public CurrencyNamingConverter converter() {
        return new CurrencyNamingConverter();
    }

    @Bean(name = "asyncCourseExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    @Bean
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("courses-nacbank", "courses-privatbank", "courses-monobank");
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

    @Bean
    public Caffeine caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(24, TimeUnit.HOURS);
    }
}