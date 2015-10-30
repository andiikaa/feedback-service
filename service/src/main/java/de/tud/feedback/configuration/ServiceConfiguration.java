package de.tud.feedback.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@EnableCaching
@Configuration
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceConfiguration {

    @Bean
    public ConcurrentTaskScheduler taskExecutor() {
        ConcurrentTaskScheduler taskScheduler = new ConcurrentTaskScheduler();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        taskScheduler.setConcurrentExecutor(executor);
        executor.setCorePoolSize(10);
        executor.initialize();
        return taskScheduler;
    }

}
