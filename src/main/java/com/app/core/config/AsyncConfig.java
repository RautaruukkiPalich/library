package com.app.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {
    public static final String TASK = "taskExecutor";
    public static final String FILE_DELETION = "fileDeletionExecutor";


    @Bean(name = TASK)
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);        // Минимальное количество потоков
        executor.setMaxPoolSize(5);         // Максимальное количество потоков
        executor.setQueueCapacity(100);     // Размер очереди
        executor.setKeepAliveSeconds(60);   // Время жизни лишних потоков

        executor.setThreadNamePrefix("media-processor-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

    @Bean(name = FILE_DELETION)
    public Executor fileDeletionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("file-deletion-");
        executor.initialize();
        return executor;
    }
}