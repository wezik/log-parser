package com.wezik.app.config;

import com.google.gson.Gson;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Getter
public class CoreConfig {

    @Value("${logs.queue.limit}")
    private Integer logsQueueLimit;

    @Value("${flag.time}")
    private Long flagTime;

    @Value("${threads}")
    private int threadCount;

    @Value("${database.chunk.limit}")
    private int chunkLimit;

    @Value("${progress.message.interval}")
    private int progressMessageInterval;

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadCount);
    }

}
