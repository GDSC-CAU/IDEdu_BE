package com.gdg.backend.config;

import com.gdg.backend.domain.documentoperation.entity.DocumentOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class MessageQueueConfig {

    @Bean
    public BlockingQueue<DocumentOperation> eventQueue() {
        // 용량 1000짜리 메시지 큐
        return new LinkedBlockingQueue<>(1000);
    }
}
