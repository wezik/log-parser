package com.wezik.app.config;

import com.google.gson.Gson;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CoreConfig {

    @Value("${logs.queue}")
    private Integer logsQueue;

    @Value("${flag.time}")
    private Long flagTime;

    @Bean
    public Gson gson() {
        return new Gson();
    }

}
